import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

//The outline of the GUI class was provided by my instructor, but I had to alter it in order to make sure it was compatible with my NeuralNet
public class GUI {

	final int imageSize = 28; // don't change this - must match the MNIST data
	final int canvasSize = imageSize * 15;
	final int brushSize = 30;

	NeuralNetwork net;
	Input[] trainingData;
	Input[] validationData;
	PaintCanvas paintCanvas;
	JLabel digitLabel;
	double desiredAccuracy;

	GUI(NeuralNetwork net, Input[] trainingData, Input[] validationData, double desiredAccuracy) {
		this.net = net;
		this.trainingData = trainingData;
		this.validationData = validationData;
		this.desiredAccuracy = desiredAccuracy;

		paintCanvas = new PaintCanvas();

		Box controls = Box.createVerticalBox();

		JButton learnButton = new JButton("Learn");
		learnButton.addActionListener(new LearnListener());

		JButton clearButton = new JButton(("Clear"));
		clearButton.addActionListener(new ClearListener());

		JButton classifyButton = new JButton("Classify");
		classifyButton.addActionListener(new ClassifyListener());

		digitLabel = new JLabel();
		digitLabel.setFont(new Font("Sans Serif", Font.BOLD, 48));
		digitLabel.setText("  ");
		Box digitBox = Box.createHorizontalBox();
		digitBox.add(Box.createGlue());
		digitBox.add(digitLabel);
		digitBox.add(Box.createGlue());

		controls.add(learnButton);
		controls.add(Box.createVerticalStrut(10));
		controls.add(clearButton);
		controls.add(Box.createVerticalStrut(10));
		controls.add(classifyButton);
		controls.add(Box.createVerticalStrut(20));
		controls.add(Box.createGlue());
		controls.add(digitBox);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(paintCanvas, BorderLayout.CENTER);
		mainPanel.add(controls, BorderLayout.WEST);

		// Create the framed window //////////////////////////////////

		JFrame frame = new JFrame("Digit Recognition");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.toFront();
		paintCanvas.clear();
	}

	class LearnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			net.learnManyExamples(trainingData, validationData, desiredAccuracy);
		}
	}

	class ClearListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			paintCanvas.clear();
			digitLabel.setText("  ");
		}
	}

	class ClassifyListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Image sampledImage = paintCanvas.bufferedImage.getScaledInstance(imageSize, imageSize, Image.SCALE_AREA_AVERAGING);
			BufferedImage finalImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_BYTE_GRAY);
			Graphics pen = finalImage.getGraphics();
			pen.drawImage(sampledImage, 0, 0, null);

			paintCanvas.bufferedImagePen.drawImage(finalImage, canvasSize - imageSize, canvasSize - imageSize, paintCanvas);
			paintCanvas.display();

			double[] inputs = new double[imageSize * imageSize];
			for (int y = 0; y < imageSize; y++) {
				for (int x = 0; x < imageSize; x++) {
					inputs[y * imageSize + x] = (finalImage.getRGB(x, y) & 0xFF) / 255.0;
				}
			}

			String classifiedCategory = net.classifyOneExample(inputs);
			System.out.println("Recognized a " + classifiedCategory);

			digitLabel.setText("" + classifiedCategory);

		}
	}

	class PaintCanvas extends Canvas {
		private BufferStrategy strategy = null;
		private final Color backgroundColor = Color.WHITE;
		private final Color foregroundColor = Color.BLACK;
		private Graphics2D bufferedImagePen;
		BufferedImage bufferedImage;

		PaintCanvas() {
			setPreferredSize(new Dimension(canvasSize, canvasSize));
			setIgnoreRepaint(true);
			Scribbler scribbler = new Scribbler();
			addMouseListener(scribbler);
			addMouseMotionListener(scribbler);
			bufferedImage = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_RGB);
			bufferedImagePen = bufferedImage.createGraphics();
			bufferedImagePen.setColor(foregroundColor);
			bufferedImagePen.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}

		public void addNotify() {
			super.addNotify();
			createBufferStrategy(2);
			strategy = getBufferStrategy();
		}

		Graphics2D getCanvasPen() {
			return (Graphics2D) strategy.getDrawGraphics();
		}

		void display() {
			getCanvasPen().drawImage(bufferedImage, 0, 0, this);
			strategy.show();
		}

		void clear() {
			bufferedImagePen.setColor(backgroundColor);
			bufferedImagePen.fillRect(0, 0, getWidth(), getHeight());
			bufferedImagePen.setColor(foregroundColor);
			display();
		}

		class Scribbler extends MouseInputAdapter {
			int prevX, prevY;

			@Override
			public void mousePressed(MouseEvent event) {
				prevX = event.getX();
				prevY = event.getY();
			}

			@Override
			public void mouseDragged(MouseEvent event) {
				int x = event.getX();
				int y = event.getY();
				bufferedImagePen.drawLine(prevX, prevY, x, y);
				prevX = x;
				prevY = y;
				display();
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

		}

	}

	static Input[] readData(String labelFileName, String imageFileName, int dataLength) {
	    DataInputStream labelStream = openFile(labelFileName, 2049);
	    DataInputStream imageStream = openFile(imageFileName, 2051);

	    Input[] examples = new Input[dataLength];

	    try {
	        int numLabels = labelStream.readInt();
	        int numImages = imageStream.readInt();
	        assert(numImages == numLabels) : "lengths of label file and image file do not match";
	        
	        int rows = imageStream.readInt();
	        int cols = imageStream.readInt();
	        assert(rows == cols) : "images in file are not square";
	        assert(rows == 28) : "images in file are wrong size";
	        
	        for (int i = 0; i < numImages; i++) {
	            int categoryLabel = Byte.toUnsignedInt(labelStream.readByte());
	            double[] inputs = new double[rows * cols];
	            for (int r = 0; r < rows; r++) {
	                for (int c = 0; c < cols; c++) {
	                    int pixel = 255 - Byte.toUnsignedInt(imageStream.readByte());
	                    inputs[r * rows + c] = pixel / 255.0;
	                }
	            }
	            examples[i] = new Input(inputs, Integer.toString(categoryLabel));
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	    return examples;
	}

	static DataInputStream openFile(String fileName, int expectedMagicNumber) {
		DataInputStream stream = null;
		try {
			stream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
			int magic = stream.readInt();
			if (magic != expectedMagicNumber) {
				throw new RuntimeException("file " + fileName + " contains invalid magic number");
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file " + fileName + " was not found");
		} catch (IOException e) {
			throw new RuntimeException("file " + fileName + " had exception: " + e);
		}
		return stream;
	}
}