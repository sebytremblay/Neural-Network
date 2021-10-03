public class Input {
	private String expectedAnswer;
	private double[] inputs;
	
	Input(double[] inputs, String expectedAnswer) {
		this.inputs = inputs;
		this.expectedAnswer = expectedAnswer;
	}
	
	public String getExpectedAnswer() {
		return this.expectedAnswer;
	}
	
	public double[] getInputs() {
		return this.inputs;
	}
}
