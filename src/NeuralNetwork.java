public class NeuralNetwork {
	private HiddenNeuron[] hiddenNeurons;
	private AnswerNeuron[] answerNeurons;
	private double[] hnOutputs;
	private double[] anOutputs;
	private double learningRate;
	private Boolean wantOutput;

	NeuralNetwork(int amountInputs, int amountHidden, String[] answerOptions, double learningRate, Boolean wantOutput) {
		// Determines whether or not output is printed
		this.wantOutput = wantOutput;
		
		// Sets learning rate
		this.learningRate = learningRate;

		// Initializes hidden neurons
		hiddenNeurons = new HiddenNeuron[amountHidden];
		for (int i = 0; i < amountHidden; i++) {
			hiddenNeurons[i] = new HiddenNeuron(amountInputs);
		}
		hnOutputs = new double[amountHidden];

		// Initializes answer neurons
		answerNeurons = new AnswerNeuron[answerOptions.length];
		for (int i = 0; i < answerOptions.length; i++) {
			answerNeurons[i] = new AnswerNeuron(amountHidden, answerOptions[i]);
		}
		anOutputs = new double[answerOptions.length];
	}

	// Runs network forward and updates output
	public void findOutputs(double[] inputs) {
		// Finds HN outputs
		for (int i = 0; i < hiddenNeurons.length; i++) {
			hnOutputs[i] = hiddenNeurons[i].output(inputs);
		}

		// Finds AN Outputs
		for (int i = 0; i < answerNeurons.length; i++) {
			anOutputs[i] = answerNeurons[i].output(hnOutputs);
		}
	}

	// Classifies one example
	public String classifyOneExample(double[] exampleInputs) {
		// Updates outputs
		findOutputs(exampleInputs);

		// Finds highest output, returns null if not over 0.7
		String finalAnswer = answerNeurons[0].getName();
		double highestOutput = anOutputs[0];
		for (int i = 0; i < anOutputs.length; i++) {
			if (highestOutput < anOutputs[i]) {
				highestOutput = anOutputs[i];
				finalAnswer = answerNeurons[i].getName();
			}
		}
		/*if (highestOutput < 0.7) {
			finalAnswer = "N/A";
		}*/

		return finalAnswer;
	}

	// Calculates accuracy on given data set
	public double calculateAccuracy(Input[] examples) {
		double numCorrect = 0;
		for (Input example : examples) {
			String finalAnswer = classifyOneExample(example.getInputs());
			if (finalAnswer.equals(example.getExpectedAnswer())) {
				numCorrect += 1;
			}
		}
		return (numCorrect / examples.length);
	}

	// Learns from one example
	public void learnOneExample(Input example) {
		// Finds neurons output for given example
		findOutputs(example.getInputs());

		// Finds each AN's correct output
		String correctAnswer = example.getExpectedAnswer();
		for (AnswerNeuron AN : answerNeurons) {
			if (AN.getName().equals(correctAnswer)) {
				AN.setCorrectOutput(1.0);
			} else {
				AN.setCorrectOutput(0.0);
			}
		}

		// Updates weights and biases for answer neurons
		for (int i = 0; i < answerNeurons.length; i++) {
			answerNeurons[i].updateErrorSignal(answerNeurons[i].getCorrectOutput(), anOutputs[i]);
			answerNeurons[i].updateBiass(learningRate);
			for (int x = 0; x < hnOutputs.length; x++) {
				answerNeurons[i].updateWeight(x, hnOutputs[x], learningRate);
			}
		}

		// Updates weights and biases for hidden neurons
		for (int i = 0; i < hiddenNeurons.length; i++) {
			double errorSignal = 0;
			for (int x = 0; x < answerNeurons.length; x++) {
				errorSignal += answerNeurons[x].getWeight(i) * answerNeurons[x].getErrorSignal();
			}
			errorSignal *= hnOutputs[i] * (1 - hnOutputs[i]);
			hiddenNeurons[i].updateBias(errorSignal * learningRate);
			for (int x = 0; x < example.getInputs().length; x++) {
				double newWeight = hiddenNeurons[i].getWeight(x)
						+ (errorSignal * example.getInputs()[x] * learningRate);
				hiddenNeurons[i].setWeight(x, newWeight);
			}
		}
	}

	// Learns many examples
	public void learnManyExamples(Input[] trainingData, Input[] validationData, double desiredAccuracy) {
		double passingRate = 0;
		double startTime = System.currentTimeMillis();
		double epochs = 0;

		System.out.println("--- Learning Started ---");
		while (passingRate < desiredAccuracy) {
			// Resets everything after certain amount of epochs
			if (epochs > 100000) {
				resetWeights();
				epochs = 0;
			}
			
			// Learns
			for (Input example : trainingData) {
				learnOneExample(example);
			}
			
			// Calculates accuracy on validation set and sends loop updates
			passingRate = calculateAccuracy(validationData);
			epochs += 1;
			if (wantOutput == true) {
				System.out.println("Epcoh #" + epochs + " | " + passingRate);
			}
		}
		System.out.println("--- Learning Finished ---");
		System.out.println("Final Learning Score: " + passingRate * 100 + "%");
		System.out.println(epochs + " epochs passed.");
		System.out.println("Time Elapsed: " + (System.currentTimeMillis() - startTime) + "ms");
		System.out.println("- - - - - - - - - - - - -");
	}

	// Resets all weights
	public void resetWeights() {
		for (HiddenNeuron HN : hiddenNeurons) {
			HN.resetWeights();
		}
		for (AnswerNeuron AN : answerNeurons) {
			AN.resetWeights();
		}
		System.out.println("Weights Reset");
	}
}