using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json;
using System.Data;
using System.IO;
using WaveBox.DataModel.Singletons;
using WaveBox.DataModel.Model;
using WaveBox.Http;

namespace WaveBox.ApiHandler.Handlers
{
	class DatabaseApiHandler : IApiHandler
	{
		private IHttpProcessor Processor { get; set; }
		private UriWrapper Uri { get; set; }
		
		public DatabaseApiHandler(UriWrapper uri, IHttpProcessor processor, User user)
		{
			Processor = processor;
			Uri = uri;
		}
		
		public void Process()
		{	
			// Try to get the update time
			string id = Uri.Parameters.ContainsKey("id") ? Uri.Parameters["id"] : null;

			if ((object)id == null)
			{
				// No id parameter, so send down the whole backup database
				long databaseLastQueryId = -1;
				string databaseFileName = Database.Backup(out databaseLastQueryId);
				if ((object)databaseFileName == null)
				{
					Processor.WriteErrorHeader();
				}
				else
				{
					Stream stream = new FileStream(databaseFileName, FileMode.Open, FileAccess.Read);
					long length = stream.Length;
					int startOffset = 0;
					
					// Handle the Range header to start from later in the file
					if (Processor.HttpHeaders.ContainsKey("Range"))
					{
						string range = (string)Processor.HttpHeaders["Range"];
						string start = range.Split(new char[]{'-', '='})[1];
						Console.WriteLine("[DATABASEAPI] Connection retried.  Resuming from {0}", start);
						startOffset = Convert.ToInt32(start);
					}

					// We send the last query id as a custom header
					IDictionary<string, string> customHeader = new Dictionary<string, string>();
					customHeader["WaveBox-LastQueryId"] = databaseLastQueryId.ToString();
					
					// Send the database file
					Processor.WriteFile(stream, startOffset, length, "application/octet-stream", customHeader);
				}
			}
			else
			{
				// Return all queries >= this id
				IDbConnection conn = null;
				IDataReader reader = null;
				IList<IDictionary<string, object>> queries = new List<IDictionary<string, object>>();
				try
				{
					conn = Database.GetQueryLogDbConnection();
					IDbCommand q = Database.GetDbCommand("SELECT * FROM query_log " +
														 "WHERE query_id >= @queryid", conn);
					q.AddNamedParam("@queryid", id);
					q.Prepare();
					reader = q.ExecuteReader();
					
					while (reader.Read())
					{
						long queryId = reader.GetInt64(0);
						string queryString = reader.GetString(1);
						string values = reader.GetString(2);

						IDictionary<string, object> query = new Dictionary<string, object>();
						query["id"] = queryId;
						query["query"] = queryString;
						query["values"] = values;

						queries.Add(query);
					}
				}
				catch (Exception e)
				{
					Console.WriteLine("[SONG(1)] " + e);
				}
				finally
				{
					Database.Close(conn, null);
				}

				try
				{
					string json = JsonConvert.SerializeObject(new DatabaseResponse(null, queries), Settings.JsonFormatting);
					Processor.WriteJson(json);
				}
				catch(Exception e)
				{
					Console.WriteLine("[DATABASEAPI(1)] ERROR: " + e);
				}
			}
		}
		
		private class DatabaseResponse
		{
			[JsonProperty("error")]
			public string Error { get; set; }
			
			[JsonProperty("queries")]
			public IList<IDictionary<string, object>> Queries { get; set; }

			public DatabaseResponse(string error, IList<IDictionary<string, object>> queries)
			{
				Error = error;
				Queries = queries;
			}
		}
	}
}
