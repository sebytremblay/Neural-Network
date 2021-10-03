import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
	// AND Net
		/*Input a = new Input(new double[] { 0, 0 }, "No");
		Input b = new Input(new double[] { 1, 0 }, "No");
		Input c = new Input(new double[] { 0, 1 }, "No");
		Input d = new Input(new double[] { 1, 1 }, "Yes");
		Input[] trainingData = { a, b, c, d };
		Input[] validationData = { a, b, c, d };
		Input[] testingData = { a, b, c, d };
		String[] options = { "No", "Yes" };
		int amountInputs = 2;
		int amountHidden = 2;
		double learningRate = 1.0;
		double desiredAccuracy = 1.00;
		Boolean wantOutput = false;
		NeuralNetwork andNet = new NeuralNetwork(amountInputs, amountHidden, options, learningRate, wantOutput);
		andNet.learnManyExamples(trainingData, validationData, desiredAccuracy);
		System.out.println("Test Score: " + andNet.calculateAccuracy(testingData) * 100 + "%");*/
		
	// XOR Net
		/*Input a = new Input(new double[] { 0, 0 }, "No");
		Input b = new Input(new double[] { 1, 0 }, "Yes");
		Input c = new Input(new double[] { 0, 1 }, "Yes");
		Input d = new Input(new double[] { 1, 1 }, "No");
		Input[] trainingData = { a, b, c, d };
		Input[] validationData = { a, b, c, d };
		Input[] testingData = { a, b, c, d };
		String[] options = { "No", "Yes" };
		int amountInputs = 2;
		int amountHidden = 150;
		double learningRate = 0.1;
		double desiredAccuracy = 1.00;
		Boolean wantOutput = false;
		NeuralNetwork xorNet = new NeuralNetwork(amountInputs, amountHidden, options, learningRate, wantOutput);
		xorNet.learnManyExamples(trainingData, validationData, desiredAccuracy);
		System.out.println("Test Score: " + xorNet.calculateAccuracy(testingData) * 100 + "%");*/
		
	// Alpaydin + Kaynak Handwritten Digit Net
		/*Input[] allData = readText(new SimpleFile("digits-train.txt"));
		// Input[] trainingData = allData;
		// Input[] validationData = allData;
		Input[] trainingData = Arrays.copyOfRange(allData, 0, allData.length - 500);
		Input[] validationData = Arrays.copyOfRange(allData, allData.length - 500, allData.length);
		Input[] testingData = readText(new SimpleFile("digits-test.txt"));
		String[] options = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		int amountInputs = 64;
		int amountHidden = 250;
		double learningRate = 0.10;
		double desiredAccuracy = 0.985;
		Boolean wantOutput = true;
		NeuralNetwork handDigitNet = new NeuralNetwork(amountInputs, amountHidden, options, learningRate, wantOutput);
		handDigitNet.learnManyExamples(trainingData, validationData, desiredAccuracy);
		System.out.println("Test Score: " + handDigitNet.calculateAccuracy(testingData) * 100 + "%");*/ 
		
	// MNIST Net
		/*Input[] allData = GUI.readData("train-labels.idx1-ubyte", "train-images.idx3-ubyte", 60000);
		Input[] trainingData = allData;
		Input[] validationData = allData;
		// Input[] trainingData = Arrays.copyOfRange(allData, 0, allData.length - 500);
		// Input[] validationData = Arrays.copyOfRange(allData, allData.length - 500, allData.length);
		// Input[] testingData = GUI.readData("t10k-labels.idx1-ubyte","t10k-images.idx3-ubyte", 0);
		String[] options = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		int amountInputs = 784;
		int amountHidden = 100;
		double learningRate = 0.07;
		double desiredAccuracy = 0.925;
		Boolean wantOutput = true;
		NeuralNetwork net = new NeuralNetwork(amountInputs, amountHidden, options, learningRate, wantOutput);
		GUI gui = new GUI(net, trainingData, validationData, desiredAccuracy);*/
	}
	
	// Reads text file and returns Input[]
	public static Input[] readText(SimpleFile file) {
		// Counts number of lines in file
		int numLines = 0;
		for (String line : file) {
			numLines += 1;
		}

		// Reads through each line and converts data into Input object
		Input[] examples = new Input[numLines];
		int counter = 0;
		for (String line : file) {
			String[] lineData = line.split(",");
			double[] inputs = new double[64];
			for (int i = 0; i < 64; i++) {
				inputs[i] = Double.parseDouble(lineData[i]);
			}
			String category = lineData[64];
			examples[counter] = new Input(inputs, category);
			counter += 1;
		}
		return examples;
	}
}