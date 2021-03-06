﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using WaveBox.DataModel.Model;
using System.IO;
using NLog;

namespace WaveBox.DataModel.Singletons
{
	public class Settings
	{
		private static Logger logger = LogManager.GetCurrentClassLogger();

		public static string settingsFileName = "wavebox.conf";
		public static string SettingsTemplatePath() { return "res" + Path.DirectorySeparatorChar + settingsFileName; }
		public static string SettingsPath() { return WaveBoxMain.RootPath() + settingsFileName; }

		public static double Version { get { return 1.0; } }

		private static SettingsData settingsModel = new SettingsData();
		public static SettingsData SettingsModel { get { return settingsModel; } }

		public static Formatting JsonFormatting { get { return settingsModel.PrettyJson ? Formatting.Indented : Formatting.None; } }

		public static short Port { get { return settingsModel.Port; } }

		public static string PodcastFolder { get { return settingsModel.PodcastFolder; } }

		public static int PodcastCheckInterval { get { return settingsModel.PodcastCheckInterval; } }

		private static List<Folder> mediaFolders;
		public static List<Folder> MediaFolders { get { return mediaFolders; } }

		public static List<string> FolderArtNames { get { return settingsModel.FolderArtNames; } }


		public static void Reload()
		{
			ParseSettings();
		}

		private static void ParseSettings()
		{
			StreamReader reader = new StreamReader(SettingsPath());
			string configFile = RemoveJsonComments(reader);

			// Grab all settings from the file
			settingsModel = JsonConvert.DeserializeObject<SettingsData>(configFile);

			// Generate Folder objects from the media folders
			mediaFolders = PopulateMediaFolders();



			dynamic json = JsonConvert.DeserializeObject(configFile);
			bool settingsChanged = false;
			
			try
			{
				string podcastFolderTemp = json.podcastFolderDoesntExist;
				settingsModel.PodcastFolder = podcastFolderTemp;
				settingsChanged = true;
			}
			catch {}

			logger.Info("[SETTINGS] settings changed: " + settingsChanged + " | port: " + settingsModel.Port);
		}

		public static bool WriteSettings(string jsonString)
		{
			dynamic json = JsonConvert.DeserializeObject(jsonString);

			bool settingsChanged = false;

			try
			{
				short? port = json.port;
				if (port != null)
				{
					settingsModel.Port = (short)port;
					settingsChanged = true;
				}
			}
			catch {}
			 
			try
			{
				if (json.mediaFolders != null)
				{
					List<string> mediaFoldersTemp = new List<string>();
					foreach (string mediaFolderString in json.mediaFolders)
					{
						mediaFoldersTemp.Add(mediaFolderString);
					}
					settingsModel.MediaFolders = mediaFoldersTemp;
					mediaFolders = PopulateMediaFolders();
					settingsChanged = true;
				}
			}
			catch {}
		
			try
			{
				string podcastFolderTemp = json.podcastFolder;
				if (podcastFolderTemp != null)
				{
					settingsModel.PodcastFolder = podcastFolderTemp;
					settingsChanged = true;
				}
			}
			catch {}

			try
			{
				bool? prettyJsonTemp = json.prettyJson;
				if (prettyJsonTemp != null)
				{
					settingsModel.PrettyJson = (bool)prettyJsonTemp;
					settingsChanged = true;
				}
			}
			catch {}

			try
			{
				int? podcastCheckIntervalTemp = json.podcastCheckInterval;
				if (podcastCheckIntervalTemp != null)
				{
					settingsModel.PodcastCheckInterval = (int)podcastCheckIntervalTemp;
					settingsChanged = true;
				}
			}
			catch {}

			try
			{
				if (json.folderArtNames != null)
				{
					List<string> folderArtNamesTemp = new List<string>();
					foreach (string artName in json.folderArtNames)
					{
						folderArtNamesTemp.Add(artName);
					}
					settingsModel.FolderArtNames = folderArtNamesTemp;
					settingsChanged = true;
				}
			}
			catch {}
			
			// Now write the settings to disk
			if (settingsChanged)
			{
				FlushSettings();
			}

			return settingsChanged;
		}

		public static void FlushSettings()
		{
			// Write the settings data model to disk
			StreamWriter settingsOut = new StreamWriter(SettingsPath());
			settingsOut.Write(JsonConvert.SerializeObject(settingsModel, Formatting.Indented));
			settingsOut.Close();
		}

		public static void SettingsSetup()
        {
            if (!File.Exists(SettingsPath()))
            {
                try
                {
                    logger.Info("[SETTINGS] " + "Setting file doesn't exist; Creating it. (WaveBox.conf)");
                    StreamReader settingsTemplate = new StreamReader(SettingsTemplatePath());
                    StreamWriter settingsOut = new StreamWriter(SettingsPath());

                    settingsOut.Write(settingsTemplate.ReadToEnd());

                    settingsTemplate.Close();
                    settingsOut.Close();
                } 
				catch (Exception e)
                {
                    logger.Info("[SETTINGS(1)] " + e);
                }
            }

			Reload();
		}

		private static List<Folder> PopulateMediaFolders()
		{
			List<Folder> folders = new List<Folder>();

			foreach (string mediaFolderString in settingsModel.MediaFolders)
			{
				Folder mediaFolder = new Folder(mediaFolderString, true);
				if (mediaFolder.FolderId == null)
				{
					mediaFolder.InsertFolder(true);
				}
				folders.Add(mediaFolder);
			}

			return folders;
		}

        private static string RemoveJsonComments(StreamReader reader)
        {
            StringBuilder js = new StringBuilder();
            string line = null;
            bool inBlockComment = false;
            bool inStringLiteral = false;

            Action<char> AppendDiscardingWhitespace = (c) => 
            {
                if(c != '\t' && c != ' ') js.Append(c);
            };

            while ((line = reader.ReadLine()) != null)
            {
                char curr, next;
                for(int i = 0; i < line.Length; i++)
                {
                    curr = line[i];

                    try
                    {
                        next = line[i + 1];
                    }
                    catch
                    {
                        //curr == '"' || curr == ','
                        if(line.Length == 1 || !inBlockComment)
                            AppendDiscardingWhitespace(curr);
                        break;
                    }

                    if(!inBlockComment)
                    {
                        if(!inStringLiteral)
                        {
                            if(curr == '"')
                            {
                                inStringLiteral = true;
                            }

                            else if(curr == '/')
                            {
                                // this is a line comment.  throw out the rest of the line.
                                if(next == '/') break;

                                // this is a block comment.  flip the block comment switch and continue to the next char
                                if(next == '*')
                                {
                                    inBlockComment = true;
                                    continue;
                                }
                            }

                            // if the combination of this char and the next char doesn't make a comment token, append to the string and continue.
                            AppendDiscardingWhitespace(curr);
                            continue;
                        }
                        else
                        {
                            if(curr == '"')
                            {
                                inStringLiteral = false;
                            }
                            js.Append(curr);
                            continue;
                        }
                    }
                    else 
                    {
                        // if we are in a block comment, make sure that we shouldn't be ending the block comment
                        if(curr == '*' && next == '/')
                        {
                            // advance the read position so we don't write the /
                            i++;
                            inBlockComment = false;
                            continue;
                        }
                    }
                }
            }
			return js.ToString();
        }
	}

	public class SettingsData
	{
		[JsonProperty("port")]
		public short Port { get; set; }
		
		[JsonProperty("mediaFolders")]
		public List<string> MediaFolders { get; set; }
		
		[JsonProperty("podcastFolder")]
		public string PodcastFolder { get; set; }
		
		[JsonProperty("prettyJson")]
		public bool PrettyJson { get; set; }
		
		[JsonProperty("podcastCheckInterval")]
		public int PodcastCheckInterval { get; set; }
		
		[JsonProperty("folderArtNames")]
		public List<string> FolderArtNames { get; set; }
	}
}
