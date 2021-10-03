import java.util.Random;

public class HiddenNeuron {
	private double[] weights;
	private double bias;
	private double errorSignal;
	private Random r = new Random();

	HiddenNeuron(int numInputs) {
		// Initializes weights and bias
		bias = (r.nextDouble() - 0.5) / 10;
		weights = new double[numInputs];
		for (int i = 0; i < numInputs; i++) {
			weights[i] = (r.nextDouble() - 0.5) / 10;
		}
		errorSignal = 0;
	}

	// Calculates output
	public double output(double[] inputs) {
		double output = bias;
		for (int i = 0; i < inputs.length; i++) {
			output += inputs[i] * weights[i];
		}
		double actualOutput = 1 / (1 + Math.exp(-output));
		return actualOutput;
	}

	// Sets error
	public void updateError(AnswerNeuron[] answerNeurons, double hnOutput) {
		
	}

	// Returns error
	public double getError() {
		return errorSignal;
	}

	// Returns weight of specified link
	public double getWeight(int index) {
		return weights[index];
	}

	// Updates weight of specified link
	public void setWeight(int index, double newWeight) {
		weights[index] = newWeight;
	}

	// Returns bias
	public double getBias() {
		return bias;
	}

	// Updates bias
	public void updateBias(double increase) {
		bias += increase;
	}
	
	// Resets Weights
	public void resetWeights() {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = (r.nextDouble() - 0.5) / 10; 
		}
		bias = (r.nextDouble() - 0.5) / 10;
	}
}