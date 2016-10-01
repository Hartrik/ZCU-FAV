package p3;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * DrawingTool
 * is designed for drawing simple graphic objects or animations.<br>
 * This public class creates and displays centered drawing area and
 * offers public methods for drawing that implements DrawingArea class.
 * <p>
 * Changes from version 1.0:
 * <ul>
 * <li>Line color setting in line() deprecated.
 * <li>setColor() method added.
 * <li>New constructor supported.
 * </ul>
 *
 * Changes from version 1.1:
 * <ul>
 * <li>The size of window matches the size of drawing area.
 * </ul>
 *
 * @author Roman Tesar  (romant@kiv.zcu.cz)
 * <br>Czech Republic - Pilsen, March 2005
 * <br>PLEASE SEND ME REPORTS OF ANY BUGS OR SUGGESTIONS YOU HAVE FOR IMPROVEMENT.
 * @version  1.2 (2014)
 */
public class DrawingTool extends JFrame {

	/** Represents area for drawing */
	private DrawingArea drawingArea;


	/**
	 * DrawingTool constructor.
	 * @param width   the drawing area width
	 * @param height  the drawing area height
	 */
	public DrawingTool(int width, int height) {
		this(width, height, Color.WHITE, false);
	}

	/**
	 * DrawingTool constructor.
	 * @param width    the drawing area width
	 * @param height   the drawing area height
	 * @param bgColor  the background color of drawing area
	 */
	public DrawingTool(int width, int height, Color bgColor) {
		this(width, height, bgColor, false);
	}

	/**
	 * DrawingTool constructor.
	 * @param width         the drawing area width
	 * @param height        the drawing area height
	 * @param antialiasing  antialiasing on/off
	 */
	public DrawingTool(int width, int height, boolean antialiasing) {
		this(width, height, Color.WHITE, antialiasing);
	}

	/**
	 * DrawingTool constructor.
	 * @param width         the drawing area width
	 * @param height        the drawing area height
	 * @param bgColor       the background color of drawing area
	 * @param antialiasing  antialiasing on/off
	 */
	public DrawingTool(int width, int height, Color bgColor, boolean antialiasing) {
		drawingArea = new DrawingArea(new Dimension(width, height), bgColor, antialiasing);
		// Add components to layout
		Container content = this.getContentPane();
		content.setLayout(new BorderLayout());
		content.add(drawingArea, BorderLayout.CENTER);
		// Prepare the main frame
		this.setTitle("DrawingTool  (" + width + "x" + height + ")");
		this.setBackground(bgColor);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		// Center the main window of DrawingTool
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(	(screen.width - this.getSize().width) / 2,
							(screen.height - this.getSize().height) / 2 );
		this.setVisible(true);
	}


	/**
	 * Draw line.
	 * @param x1     starting line position (coordinate x)
	 * @param y1     starting line position (coordinate y)
	 * @param x2     ending line position  (coordinate x)
	 * @param y2     ending line position  (coordinate y)
	 */
	public void line(int x1, int y1, int x2, int y2) {
		drawingArea.line(new Point(x1, y1), new Point(x2, y2));
	}

	/**
	 * Clear drawing area.
	 */
	public void clear() {
		drawingArea.clear();
	}

	/**
	 * Set foreground color of drawing area.
	 * @param fgColor  the foreground color of drawing area
	 */
	public void setColor(Color fgColor) {
		drawingArea.setColor(fgColor);
	}

	/**
	 * Disable displaying of drawing area updates.
	 */
	public void stopUpdate() {
		drawingArea.stopUpdate();
	}

	/**
	 * Enable displaying of drawing area updates.
	 */
	public void startUpdate() {
		drawingArea.startUpdate();
	}
}


/**
 * This inner class represents drawing area and implements methods for drawing
 * available from DrawingTool class. Double buffering is used to get rid of the
 * flickering problem.
 *
 * @author Roman Tesar  (romant@kiv.zcu.cz)
 * <br>Czech Republic - Pilsen, March 2005
 * <br>PLEASE SEND ME REPORTS OF ANY BUGS OR SUGGESTIONS YOU HAVE FOR IMPROVEMENT.
 * @version  1.1
 */
class DrawingArea extends JPanel {

	/** dimension of drawing area */
	private Dimension dimension;
	/** back buffer */
	private BufferedImage image = null;
	/** graphic context of the back buffer */
	private Graphics2D gcImage = null;
	/** antialiasing on/off */
	private boolean antialiasing;
	/** drawing to screen on/off */
	private boolean invalidate = false;

	/**
	 * DrawingArea constructor.
	 * @param dimension     the drawing area dimension
	 * @param bgColor       the background color of drawing area
	 * @param antialiasing  antialiasing on/off
	 */
	public DrawingArea(Dimension dimension, Color bgColor, boolean antialiasing) {
		this.dimension = dimension;
		this.setBackground(bgColor);
		this.antialiasing = antialiasing;
		setPreferredSize(dimension);
		this.setVisible(true);
	}


	/**
	 * Initialize back buffer.
	 */
	private void init() {
		if (image == null) {
	        image = (BufferedImage)(this.createImage(dimension.width, dimension.height));
	        gcImage = image.createGraphics();
	        gcImage.setBackground(this.getBackground());
	        gcImage.clearRect(0, 0, dimension.width, dimension.height);
	        if (antialiasing) {
	        	gcImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            gcImage.setRenderingHint(RenderingHints.KEY_RENDERING,	RenderingHints.VALUE_RENDER_QUALITY);
	        }
		}
	}

	/**
	 * Draw back buffer to screen when the JPanel component is repainted.
	 * <br>See JavaDoc JPanel classs for details.
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (invalidate == false && image != null)
			((Graphics2D) g).drawImage(image, null, 0, 0);
	}

	/**
	 * Draw back buffer to screen.
	 */
	private void drawOnScreen() {
		if (invalidate == false)
			((Graphics2D) this.getGraphics()).drawImage(image, null, 0, 0);
	}

	/**
	 * Draw line.
	 * @param startPoint  starting line point (x,y)
	 * @param endPoint    ending line point (x,y)
	 */
	public void line(Point startPoint, Point endPoint) {
		init();
		gcImage.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		drawOnScreen();
	}

	/**
	 * Set foreground color of drawing area.
	 * @param fgColor  the foreground color of drawing area
	 */
	public void setColor(Color fgColor) {
		init();
		gcImage.setColor(fgColor);
	}

	/**
	 * Clear drawing area.
	 */
	public void clear() {
		init();
		gcImage.clearRect(0, 0, dimension.width, dimension.height);
		drawOnScreen();
	}

	/**
	 * Disable displaying of drawing area updates.
	 */
	public void stopUpdate() {
		init();
		invalidate = true;
	}

	/**
	 * Enable displaying of drawing area updates.
	 */
	public void startUpdate() {
		init();
		invalidate = false;
		drawOnScreen();
	}
}
