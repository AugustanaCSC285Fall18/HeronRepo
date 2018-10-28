package datamodel;


import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

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
	
	/**
	 * This method constructs the video object given the filePath in a string. If the file
	 * does not exist then a FileNotFoundException is thrown
	 * @param filePath -the file that is going to be used
	 * @throws FileNotFoundException -if there is no file that corresponds to the file name
	 */
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
	
	/**
	 * This method returns the width of the video
	 * @return -the width of the frame
	 */
	public int getFrameWidth() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	
	/**
	 * This method returns the height of the video
	 * @return -the height of the frame
	 */
	public int getFrameHeight() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
	
	/**
	 * This method returns the frames per second
	 * @return -the frame rate of the video
	 */
	public double getFrameRate() {
		return videoCap.get(Videoio.CAP_PROP_FPS);
	}
	
	/**
	 * This method gives the duration of the video in seconds
	 * @return -the duration of the video
	 */
	public double getDurationInSeconds() {
		return (endFrameNum- startFrameNum) / getFrameRate();
	}
	
	/**
	 * This method converts the number of frames into seconds based on the frame rate
	 * @param numFrames -the number of frames
	 * @return -the number of seconds
	 */
	public double convertFrameNumsToSeconds(int numFrames) {
		return numFrames / getFrameRate();
	}
	
	/**
	 * This method converts the number of seconds into frames based on the frame rate
	 * @param numSecs -the number of seconds
	 * @return -the number of frames
	 */
	public int convertSecondsToFrameNums(double numSecs) {
		return (int) Math.round(numSecs * getFrameRate());
	}
	
	/**
	 * This method sets the VideoCapture based on the filePath 
	 */
	public void setVideo() {
		videoCap.open(filePath);
		videoCap.set(Videoio.CAP_PROP_POS_FRAMES, 0);
	}
	
	/**
	 * This method gives the filePath of the file
	 * @return -the file in a string
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * This method gives the VideoCapture object
	 * @return -the VideoCapture associated with the video
	 */
	public VideoCapture getVideoCap() {
		return videoCap;
	}
	
	/**
	 * This method reads the frame and returns it
	 * @return -the frame being read
	 */
	public Mat readFrame() {
		Mat frame = new Mat();
		videoCap.read(frame);
		return frame;
	}
	
	/**
	 * This method sets the current frame number based on the parameter
	 * @param newFrame -the new current frame number
	 */
	public void setCurrentFrameNum(int newFrame) {
		videoCap.set(Videoio.CV_CAP_PROP_POS_FRAMES, (double) newFrame); 
	}
	
	/**
	 * This method gives the current frame number
	 * @return -the current frame number
	 */
	public int getCurrentFrameNum() {
		return (int) videoCap.get(Videoio.CV_CAP_PROP_POS_FRAMES); 
	}
	
	/**
	 * This method allows the user to set the start frame number based on the parameter
	 * @param start -the start frame number
	 */
	public void setStartFrameNum(int start) {
		startFrameNum = start;
	}
	
	/**
	 * This method gives the start frame number
	 * @return -the start frame number
	 */
	public int getStartFrameNum() {
		return startFrameNum;
	}
	
	/**
	 * This method allows the user to set the end frame number
	 * @param end -the end frame number
	 */
	public void setEndFrameNum(int end) {
		endFrameNum = end;
	}
	
	/**
	 * This method gives the end frame number
	 * @return -the end frame number
	 */
	public int getEndFrameNum() {
		return endFrameNum;
	}
	
	/**
	 * This method allows the user to set the empty frame number
	 * @param emptyFrameNum -the empty frame number
	 */
	public void setEmptyFrameNum(int emptyFrameNum) {
		this.emptyFrameNum = emptyFrameNum;
	}
	
	/**
	 * This method gives the the empty frame number
	 * @return -the empty frame number
	 */
	public int getEmptyFrameNum() {
		return emptyFrameNum;
	}
	
	/**
	 * This method gives the total number of frames in the video
	 * @return -the total number of frames
	 */
	public int getTotalNumFrames() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_COUNT);
	}
	

	/**
	 * This method sets the x pixels per cm based on the parameter
	 * @param ratio -the ratio of pixels per cm in x values
	 */

	public void setXPixelsPerCm(double ratio) {
		xPixelsPerCm = ratio;
	}
	

	/**
	 * This method gives the x pixels per cm
	 * @return -the x pixels per cm
	 */

	public double getXPixelsPerCm() {
		return xPixelsPerCm;
	}
	

	/**
	 * This method sets the y pixels per cm based on the parameter
	 * @param ratio -the ratio of pixels per cm in y values
	 */

	public void setYPixelsPerCm(double ratio) {
		yPixelsPerCm = ratio;
	}
	
	/**
	 * This method gives the y pixels per cm
	 * @return -the y pixels per cm
	 */
	public double getYPixelsPerCm() {
		return yPixelsPerCm;
	}
	
	/**
	 * This method gives the average pixels per cm of x and y values
	 * @return -the average ratios for x and y values
	 */
	public double getAvgPixelsPerCm() {
		return (xPixelsPerCm + yPixelsPerCm) / 2;
	}
	
	/**
	 * Gets the horizontal and vertical size of the
	 * selected area.
	 * @return The selected area.
	 */
	public Rectangle2D getArenaBounds() {
		return trackArea;
	}
	
	/**
	 * Sets the selected area as the arena within
	 * which tracking occurs.
	 * @param trackArea The selected area.
	 */
	public void setArenaBounds(Rectangle2D trackArea) {
		this.trackArea = trackArea;
	}
	
	/**
	 * Checks to see if the point is in the selected
	 * arena for tracking.
	 * @param x The horizontal position of the point.
	 * @param y The vertical position of the point.
	 * @return true if the point is within the arena
	 *         bounds, and false otherwise.
	 */
	public boolean inRectangle(double x, double y) {
		return trackArea.contains(x, y);
	}
	
	/**
	 * Gets the width of the video file in pixels.
	 * @return the video's width in pixels.
	 */
	public int getWidth() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	
	/**
	 * Gets the height of the video file in pixels.
	 * @return The video's height in pixels.
	 */
	public int getHeight() {
		return (int) videoCap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
	
	/**
	 * Sets up the VideoCapture method.
	 * @throws FileNotFoundException if the selected file
	 * 								 cannot be found.
	 */
	synchronized void connectVideoCapture() throws FileNotFoundException {
		this.videoCap = new VideoCapture(filePath);
		if (!videoCap.isOpened()) {
			throw new FileNotFoundException("Unable to open video file: " + filePath);
		}
	}
	
	/**
	 * Grabs the next frame from the video file
	 * and displays it.
	 * @return the next frame of the video file.
	 */
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
					
				}

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return frame;
	}
	
	/**
	 * Sets the selected point as the origin (0,0) from
	 * which to start measuring positions.
	 * @param x The horizontal position of the selected point.
	 * @param y The vertical position of the selected point.
	 */
	public void setCenterPoint(double x, double y) {
		this.center= new Point2D(x,y);
	}
	
	/**
	 * Gets the 
	 * @return
	 */
	public Point2D getCenterPoint() {
		return this.center;
	}
	
}
