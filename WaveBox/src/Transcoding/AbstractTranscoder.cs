using System;
using System.IO;
using System.Threading;
using WaveBox.DataModel.Model;
using System.Diagnostics;
using NLog;

namespace WaveBox.Transcoding
{
	public abstract class AbstractTranscoder : ITranscoder
	{		
		private static Logger logger = LogManager.GetCurrentClassLogger();

		protected ITranscoderDelegate TranscoderDelegate { get; set; }

		public IMediaItem Item { get; set; }

		public TranscodeState State { get; set; }

		// This is either a TranscodeQuality enum value or if higher, a constant bitrate
		public uint Quality { get; set; }

		public int ReferenceCount { get; set; }

		public bool IsDirect { get; set; }

		public abstract TranscodeType Type { get; }

		public abstract string MimeType { get; }

		public abstract string OutputExtension { get; }

		public abstract uint? EstimatedBitrate { get; }

		public abstract string Codec { get; }

		public abstract string Command { get; }
		
		public abstract string Arguments { get; }

		public uint OffsetSeconds { get; set; }
		
		public uint LengthSeconds { get; set; }
		
		public Thread TranscodeThread { get; set; }
		public Process TranscodeProcess { get; set; }

		public string OutputFilename
		{
			get
			{
				return Item.ItemTypeId + "_" + Item.ItemId + "_" + Type + "_" + Quality + "." + OutputExtension;
			}
		}

		public string OutputPath 
		{
			get 
			{ 
				if (Item != null)
	        	{
	            	string path = IsDirect ? "-" : TranscodeManager.TRANSCODE_PATH + Path.DirectorySeparatorChar + OutputFilename;
	            	//log2File(INFO, "transcoding to " + path);
					return path;
	        	}

	        	return null; 
			} 
		}

		public long? EstimatedOutputSize 
		{ 
			get 
			{
				if (Item != null)
				{
                    var f = new FileInfo(Item.FilePath);
                    float ratio = (float)EstimatedBitrate / (float)Item.Bitrate;
                    Console.WriteLine("Bitrate ratio: {0}, Item filesize: {1}, Estimated size: {2}", ratio, f.Length, (long)(f.Length * ratio));
                    return (long)(f.Length * ratio);
					//logger.Info("Item.Duration: " + Item.Duration + "  EstimatedBitrate: " + EstimatedBitrate);
				    //return (long)LengthSeconds * (long)(EstimatedBitrate * 128);
				}
	        	return null;
			}
		}

		public AbstractTranscoder(IMediaItem item, uint quality, bool isDirect, uint offsetSeconds, uint lengthSeconds)
	    {
			State = TranscodeState.None;
	        Item = item;
	        Quality = quality;
			IsDirect = isDirect;
			OffsetSeconds = offsetSeconds;
			LengthSeconds = lengthSeconds;
	    }

	    public void CancelTranscode()
	    {
	        if (TranscodeProcess != null)
	        {
				logger.Info("[TRANSCODE] cancelling transcode for " + Item.FileName);

				// Kill the process
	            TranscodeProcess.Kill();
				TranscodeProcess = null;

				// Wait for the thread to die
				TranscodeThread.Join();
				TranscodeThread = null;
				
				// Set the state
				State = TranscodeState.Canceled;

				// Inform the delegate
                if ((object)TranscoderDelegate != null)
                    TranscoderDelegate.TranscodeFailed(this);
	        }
	    }

	    public void StartTranscode()
		{
			if ((object)TranscodeThread != null || (object)TranscodeProcess != null) 
				return;

			logger.Info("[TRANSCODE] starting transcode for " + Item.FileName);

			// Set the state
			State = TranscodeState.Active;

			// Delete any existing file of this name
			if(File.Exists(OutputPath))
               File.Delete(OutputPath);

			// Start a new thread for the transcode
			TranscodeThread = new Thread(new ThreadStart(Run));
			TranscodeThread.IsBackground = true;
			TranscodeThread.Start();
	    }

		public virtual void Run()
	    {
			try 
			{
				logger.Info("[TRANSCODE] Forking the process");
				logger.Info("[TRANSCODE] " + Command + " " + Arguments);

				// Fork the process
			    TranscodeProcess = new Process();
				TranscodeProcess.StartInfo.FileName = Command;
				TranscodeProcess.StartInfo.Arguments = Arguments;
				TranscodeProcess.StartInfo.UseShellExecute = false;
				TranscodeProcess.StartInfo.RedirectStandardOutput = true;
				TranscodeProcess.StartInfo.RedirectStandardError = true;
				TranscodeProcess.Start();

				logger.Info("[TRANSCODE] Waiting for process to finish");

				// Block until done
				TranscodeProcess.WaitForExit();

				logger.Info("[TRANSCODE] Process finished");
			}
			catch (Exception e) 
			{
				logger.Info("\t" + "[TRANSCODE] Failed to start transcode process " + e);

				// Set the state
				State = TranscodeState.Failed;

			    // Inform the delegate
                if ((object)TranscoderDelegate != null)
                    TranscoderDelegate.TranscodeFailed(this);

                return;
			}

			if (TranscodeProcess != null)
			{
			    int exitValue = TranscodeProcess.ExitCode;
			    logger.Info("[TRANSCODE] exit value " + exitValue);

				if (exitValue == 0)
				{
					State = TranscodeState.Finished;

                    if ((object)TranscoderDelegate != null)
                        TranscoderDelegate.TranscodeFinished(this);
				}
			    else
				{
					State = TranscodeState.Failed;
			        
                    if ((object)TranscoderDelegate != null)
                        TranscoderDelegate.TranscodeFailed(this);
				}
			}
	    }

		public override bool Equals(Object obj)
	    {
			// If they are the exact same object, return true
			if (Object.ReferenceEquals(this, obj))
			{
				return true;
			}
			else if (IsDirect)
			{
				// If this is a direct transcoder, only use reference equality
				return false;
			}

	        // If parameter is null return false.
	        if ((object)obj == null)
	        {
	            return false;
	        }

			// If the types don't match exactly, return false
			if (this.GetType() != obj.GetType())
			{
				return false;
			}

			// If parameter cannot be cast to AbstractTranscoder return false.
	        AbstractTranscoder op = obj as AbstractTranscoder;
	        if ((object)op == null)
	        {
	            return false;
	        }

	        // Return true if the fields match:
	        return Equals(op);
	    }

	    public bool Equals(AbstractTranscoder op)
	    {
	        // If parameter is null return false:
	        if ((object)op == null)
	        {
	            return false;
	        }

	        // Return true if they match
			return Item.Equals(op.Item) && Type == op.Type && Quality == op.Quality;
	    }

	    public override int GetHashCode()
	    {
			int hash = 13;
			hash = (hash * 7) + (Item == null ? Item.GetHashCode() : 0);
			hash = (hash * 7) + Type.GetHashCode();
			hash = (hash * 7) + Quality.GetHashCode();
			return hash;
	    }
	}
}

