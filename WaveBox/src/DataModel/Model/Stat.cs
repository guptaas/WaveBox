using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using WaveBox.DataModel.Model;
using WaveBox.DataModel.Singletons;
using System.IO;
using TagLib;
using Newtonsoft.Json;
using System.Diagnostics;
using NLog;

namespace WaveBox.DataModel.Model
{
	public enum StatType
	{
		PLAYED = 0,
		Unknown = 2147483647 // Int32.MaxValue used for database compatibility
	}

	public static class Stat
	{
		private static Logger logger = LogManager.GetCurrentClassLogger();

		// Timestamp is UTC unixtime
		public static bool RecordStat(int itemId, StatType statType, long timeStamp)
		{
			IDbConnection conn = null;
			IDataReader reader = null;
			bool success = false;
			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("INSERT INTO stat (time_stamp, item_id, stat_type) " +
				                                     "VALUES (@timestamp, @itemid, @stattype)", conn);
				q.AddNamedParam("@timestamp", timeStamp);
				q.AddNamedParam("@itemid", itemId);
				q.AddNamedParam("@stattype", (int)statType);
				q.Prepare();
				
				success = q.ExecuteNonQueryLogged() > 0;
			}
			catch (Exception e)
			{
				logger.Error("[SONG(4)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}
			
			return success;
		}

		public static bool RecordStat(this IItem item, StatType statType, long timeStamp)
		{
			return (object)item.ItemId == null ? false : Stat.RecordStat((int)item.ItemId, statType, timeStamp);
		}
	}
}

