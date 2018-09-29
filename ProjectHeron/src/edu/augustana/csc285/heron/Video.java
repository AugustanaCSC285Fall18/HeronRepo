package edu.augustana.csc285.heron;

import java.awt.Rectangle;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Video {
	private double xPixelsPerCm;
	private double yPixelsPerCm;
	private VideoCapture videoCap;
	private File file;
	private int startFrameNum;
	private int endFrameNum;
	private Rectangle trackArea;
	
	public Video(VideoCapture videoCap, File file) {
		this.file = file;
		this.videoCap = videoCap;
	}
	
	public double getDurationInSeconds() {
		return (endFrameNum- startFrameNum) / videoCap.get(Videoio.CV_CAP_PROP_XI_FRAMERATE);
	}
	
	public void setVideo() {
		videoCap.open(file.getAbsolutePath());
		videoCap.set(Videoio.CAP_PROP_POS_FRAMES, 0);
	}
	
	public VideoCapture getVideoCap() {
		return videoCap;
	}
	
	public void setStartTime(int start) {
		startFrameNum = start;
	}
	
	public int getStartTime() {
		return startFrameNum;
	}
	
	public void setEndTime(int end) {
		endFrameNum = end;
	}
	
	public int getEndTime() {
		return endFrameNum;
	}
	
	public void setXPixelsPerCm(double ratio) {
		xPixelsPerCm = ratio;
	}
	
	public double getXPixelsPerCm() {
		return xPixelsPerCm;
	}
	
	public void setYPixelsPerCm(double ratio) {
		yPixelsPerCm = ratio;
	}
	
	public double getYPixelsPerCm() {
		return yPixelsPerCm;
	}
	
	public void setFile(File newFile) {
		file = newFile;
	}
	public File getFile() {
		return file;
	}
	public Mat grabFrame() {
		// init everything
		Mat frame = new Mat();

		// check if the capture is open
		if (videoCap.isOpened()) {
			try {
				// read the current frame
				videoCap.read(frame);

				// if the frame is not empty, process it
				if (!frame.empty()) {
					// Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				}

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return frame;
	}
		
}
