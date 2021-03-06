﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using WaveBox.DataModel.Singletons;
using WaveBox.DataModel.Model;
using System.Security.Cryptography;
using NLog;

namespace WaveBox.DataModel.Model
{
	public class User
	{
		private static Logger logger = LogManager.GetCurrentClassLogger();

		public int? UserId { get; set; }
		public string UserName { get; set; }
		public string PasswordHash { get; set; }
		public string PasswordSalt { get; set; }
		public string SessionId { get; set; }
		public string LastfmSession { get; set; }

		public static string UserNameForSessionid(string sessionId)
		{
			string userName = null;
			IDbConnection conn = null;
			IDataReader reader = null;

			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("SELECT user.user_name FROM session JOIN user USING (user_id) WHERE session_id = @sessionid", conn);
				q.AddNamedParam("@sessionid", sessionId);
				q.Prepare();
				reader = q.ExecuteReader();

				if (reader.Read())
				{
					userName = reader.GetString(0);
				}
			}
			catch (Exception e)
			{
				logger.Error("[USER(1)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}

			return userName;
		}
		
		public User()
		{
		}

		public User(int userId)
		{
			UserId = userId;

			IDbConnection conn = null;
			IDataReader reader = null;

			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("SELECT * FROM user WHERE user_id = @userid", conn);
				q.AddNamedParam("@userid", UserId);
				q.Prepare();
				reader = q.ExecuteReader();

				if (reader.Read())
				{
					SetPropertiesFromQueryReader(reader);
				}
			}
			catch (Exception e)
			{
				logger.Error("[USER(1)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}
		}

		public User(string userName)
		{
			UserName = userName;

			IDbConnection conn = null;
			IDataReader reader = null;

			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("SELECT * FROM user WHERE user_name = @username", conn);
				q.AddNamedParam("@username", userName);
				q.Prepare();
				reader = q.ExecuteReader();

				if (reader.Read())
				{
					SetPropertiesFromQueryReader(reader);
				}
			}
			catch (Exception e)
			{
				logger.Error("[USER(2)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}
		}

		private void SetPropertiesFromQueryReader(IDataReader reader)
		{
			UserId = reader.GetInt32(reader.GetOrdinal("user_id"));
			UserName = reader.GetString(reader.GetOrdinal("user_name"));
			PasswordHash = reader.GetString(reader.GetOrdinal("user_password"));
			PasswordSalt = reader.GetString(reader.GetOrdinal("user_salt"));

			if (reader.GetValue(reader.GetOrdinal("user_lastfm_session")) != DBNull.Value)
			{
				LastfmSession = reader.GetString(reader.GetOrdinal("user_lastfm_session"));
			}
			else
			{
				LastfmSession = null;
			}
		}

		private static string Sha1(string sumthis)
		{
			if (sumthis == "" || sumthis == null)
			{
				return "";
			}

			SHA1CryptoServiceProvider provider = new SHA1CryptoServiceProvider();
			return BitConverter.ToString(provider.ComputeHash(Encoding.ASCII.GetBytes(sumthis))).Replace("-", "");
		}

		private static string ComputePasswordHash(string password, string salt)
		{
			//long startTime = DateTime.Now.Ticks;
			string hash = Sha1(password + salt);

			for (int i = 0; i < 50; i++)
			{
				hash = Sha1(hash + salt);
			}

			return hash;
		}

		private static string GeneratePasswordSalt()
		{
			return Sha1(Convert.ToString(DateTime.Now.Ticks));
		}

		public void UpdatePassword(string password)
		{
			IDbConnection conn = null;
			IDataReader reader = null;

			string salt = GeneratePasswordSalt();
			string hash = ComputePasswordHash(password, salt);

			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("UPDATE user SET user_password = @hash, user_salt = @salt WHERE user_name = @username", conn);
				q.AddNamedParam("@hash", hash);
				q.AddNamedParam("@salt", salt);
				q.AddNamedParam("@username", UserName);
				q.Prepare();
				q.ExecuteNonQuery();
			}
			catch (Exception e)
			{
				logger.Error("[USER(3)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}

			PasswordHash = hash;
			PasswordSalt = salt;
		}

		public void UpdateLastfmSession(string sessionKey)
		{
			IDbConnection conn = null;
			IDataReader reader = null;

			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("UPDATE user SET user_lastfm_session = @session WHERE user_name = @username", conn);
				q.AddNamedParam("@session", sessionKey);
				q.AddNamedParam("@username", UserName);
				q.Prepare();
				q.ExecuteNonQuery();
			}
			catch (Exception e)
			{
				logger.Error("[USER(4)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}

			LastfmSession = sessionKey;
		}

		public static User CreateUser(string userName, string password)
		{
			int? itemId = Item.GenerateItemId(ItemType.User);
			if (itemId == null)
				return null;

			string salt = GeneratePasswordSalt();
			string hash = ComputePasswordHash(password, salt);

			IDbConnection conn = null;
			IDataReader reader = null;
			try
			{
				conn = Database.GetDbConnection();
				IDbCommand q = Database.GetDbCommand("INSERT OR IGNORE INTO user (user_id, user_name, user_password, user_salt) VALUES (@userid, @username, @userhash, @usersalt)", conn);
				q.AddNamedParam("@userid", itemId);
				q.AddNamedParam("@username", userName);
				q.AddNamedParam("@userhash", hash);
				q.AddNamedParam("@usersalt", salt);
				q.Prepare();

				q.ExecuteNonQuery();
			}
			catch (Exception e)
			{
				logger.Error("[USER(5)] " + e);
			}
			finally
			{
				Database.Close(conn, reader);
			}

			return new User(userName);
		}

		public bool Authenticate(string password)
		{
			string hash = ComputePasswordHash(password, PasswordSalt);
			return hash == PasswordHash;
		}

		public bool CreateSession(string password, string clientName)
		{
			if (Authenticate(password))
			{
				// Generate a random string to seed the SHA1 hash, rather than using 
				// something like the current system time which would make it easier to 
				// guess or brute force session ids
				string randomString = RandomString(100);
				SessionId = Sha1(randomString);

				IDbConnection conn = null;
				IDataReader reader = null;
				try
				{
					conn = Database.GetDbConnection();
					IDbCommand q = Database.GetDbCommand("INSERT INTO session (session_id, user_id, client_name) VALUES (@sessionid, @userid, @clientname)", conn);
					q.AddNamedParam("@sessionid", SessionId);
					q.AddNamedParam("@userid", UserId);
					q.AddNamedParam("@clientname", clientName);
					q.Prepare();
					
					int affected = q.ExecuteNonQuery();
					if (affected > 0)
					{
						return true;
					}
				}
				catch (Exception e)
				{
					logger.Info("[USER(6)] " + e);
					return false;
				}
				finally
				{
					Database.Close(conn, reader);
				}
			}
			return false;
		}

		private readonly Random rng = new Random();
		private const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789!@#$%^&*()";
		private string RandomString(int size)
		{
		    char[] buffer = new char[size];
		    for (int i = 0; i < size; i++)
		    {
		        buffer[i] = chars[rng.Next(chars.Length)];
		    }
		    return new string(buffer);
		}
	}
}
