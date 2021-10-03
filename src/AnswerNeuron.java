import java.util.Random;

public class AnswerNeuron {
	private double bias;
	private double[] weights;
	private double correctOutput;
	private double errorSignal;
	private String name;
	private Random r = new Random();

	AnswerNeuron(int numInputs, String name) {
		// Initializes weights
		bias = (r.nextDouble() - 0.5) / 10;
		weights = new double[numInputs];
		for (int i = 0; i < numInputs; i++) {
			weights[i] = (r.nextDouble() - 0.5) / 10;
		}
		// Initializes expected result
		this.name = name;
		correctOutput = 0;
	}
	
	// Finds output of neuron
	public double output(double[] inputs) {
		double output = bias;
		for (int i = 0; i < inputs.length; i++) {
			output += inputs[i] * weights[i];
		}
		double actualOutput = 1 / (1 + Math.exp(-output));
		return actualOutput;
	}
	
	// Sets error signal
	public void updateErrorSignal(double correctOutput, double actualOutput) {
		this.errorSignal = (correctOutput - actualOutput) * (actualOutput) * (1 - actualOutput);
	}
	
	// Returns error signal
	public double getErrorSignal() {
		return this.errorSignal;
	}

	// Returns weight of specified link
	public double getWeight(int index) {
		return weights[index];
	}

	// Updates weight of specified link
	public void updateWeight(int index, double hnOutput, double learningRate) {
		double newWeight = weights[index] + (errorSignal * hnOutput * learningRate); 
		weights[index] = newWeight;
	}

	// Returns bias
	public double getBias() {
		return bias;
	}

	// Updates bias
	public void updateBiass(double learningRate) {
		bias += errorSignal * learningRate;
	}

	// Returns correct output
	public double getCorrectOutput() {
		return correctOutput;
	}

	// Updates correctOutput
	public void setCorrectOutput(double correctOutput) {
		this.correctOutput = correctOutput;
	}
	
	// Returns name of neuron
	public String getName() {
		return name;
	}
	
	// Resets weights
	public void resetWeights() {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = (r.nextDouble() - 0.5) / 10;
		}
		bias = (r.nextDouble() - 0.5) / 10;
	}
}