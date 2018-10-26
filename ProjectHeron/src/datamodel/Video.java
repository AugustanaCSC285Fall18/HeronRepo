package datamodel;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Video {
	private double xPixelsPerCm;
	private double yPixelsPerCm;
	private VideoCapture videoCap;
	private String filePath;
	private int emptyFrameNum;
	private int startFrameNum;
	private int endFrameNum;
	private Rectangle2D trackArea;
	private Point2D center;
	private ProjectData project;
	
	public Video(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		this.videoCap = new VideoCapture(filePath);
		if (!videoCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
		emptyFrameNum = -1;
		endFrameNum = -1;
		startFrameNum = -1;
		double frameWidth = videoCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
		double frameHeight = videoCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
		trackArea = new Rectangle2D(0,0,frameWidth,frameHeight);
	}
	
	public int getFrameWidth() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	
	public int getFrameHeight() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
	
	public double getFrameRate() {
		return videoCap.get(Videoio.CAP_PROP_FPS);
	}
	
	public double getDurationInSeconds() {
		return (endFrameNum- startFrameNum) / getFrameRate();
	}
	
	public double convertFrameNumsToSeconds(int numFrames) {
		return numFrames / getFrameRate();
	}
	
	public int convertSecondsToFrameNums(double numSecs) {
		return (int) Math.round(numSecs * getFrameRate());
	}
	
	public void setVideo() {
		videoCap.open(filePath);
		videoCap.set(Videoio.CAP_PROP_POS_FRAMES, 0);
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public VideoCapture getVideoCap() {
		return videoCap;
	}
	public Mat readFrame() {
		Mat frame = new Mat();
		videoCap.read(frame);
		return frame;
	}
	
	public void setCurrentFrameNum(int newFrame) {
		videoCap.set(Videoio.CV_CAP_PROP_POS_FRAMES, (double) newFrame); 
	}
	
	public int getCurrentFrameNum() {
		return (int) videoCap.get(Videoio.CV_CAP_PROP_POS_FRAMES); 
	}
	
	public void setStartFrameNum(int start) {
		startFrameNum = start;
	}
	
	public int getStartFrameNum() {
		return startFrameNum;
	}
	
	public void setEndFrameNum(int end) {
		endFrameNum = end;
	}
	
	public int getEndFrameNum() {
		return endFrameNum;
	}
	
	public void setEmptyFrameNum(int emptyFrameNum) {
		this.emptyFrameNum = emptyFrameNum;
	}
	
	public int getEmptyFrameNum() {
		return emptyFrameNum;
	}
	public int getTotalNumFrames() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_COUNT);
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
	
	public double getAvgPixelsPerCm() {
		return (xPixelsPerCm + yPixelsPerCm) / 2;
	}
	
	public Rectangle2D getArenaBounds() {
		return trackArea;
	}
	
	public void setArenaBounds(Rectangle2D trackArea) {
		this.trackArea = trackArea;
	}
	
	public boolean inRectangle(double x, double y) {
		return trackArea.contains(x, y);
//		return x >= trackArea.getMinX() && x <= trackArea.getMinX() + trackArea.getWidth() &&
//			y >= trackArea.getY() && y <= trackArea.getY() + trackArea.getHeight();
	}
	public int getWidth() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	public int getHeight() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
	
	synchronized void connectVideoCapture() throws FileNotFoundException {
		this.videoCap = new VideoCapture(filePath);
		if (!videoCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
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
	
	public void setCenterPoint(double x, double y) {
		this.center= new Point2D(x,y);
	}
	
	public Point2D getCenterPoint() {
		return this.center;
	}
	
}
