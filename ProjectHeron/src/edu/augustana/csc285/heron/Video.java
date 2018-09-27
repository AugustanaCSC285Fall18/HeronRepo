package edu.augustana.csc285.heron;

import java.awt.Rectangle;

public class Video {
	private double frameRate;
	private double xPixelsPerCm;
	private double yPixelsPerCm;
	private int totalNumFrames;
	private String filePath;
	private int startFrameNum;
	private int endFrameNum;
	private Rectangle trackArea;
	
	public Video(double frameRate, double xPixelsPerCm, double yPixelsPerCm, int totalNumFrames, String filePath,
			int startFrameNum, int endFrameNum, Rectangle trackArea) {
		this.frameRate = frameRate;
		this.xPixelsPerCm = xPixelsPerCm;
		this.yPixelsPerCm = yPixelsPerCm;
		this.totalNumFrames = totalNumFrames;
		this.filePath = filePath;
		this.startFrameNum = startFrameNum;
		this.endFrameNum = endFrameNum;
		this.trackArea = trackArea;
	}
	
	public double getDurationInSeconds() {
		return (endFrameNum- startFrameNum) / frameRate;
	}
}
