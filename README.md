# CoresetSlidingWindows
This repository contains a Java implementation of the algorithms
presented in *"Dimensionality-adaptive k-center in sliding windows"*.
If you find this software useful for your research, please cite the paper [[1]](#1).

### Abstract
In this paper we present a novel streaming algorithm for the k-center clustering problem for general metric spaces under the sliding window model. The algorithm maintains a small coreset which, at any time, allows to compute a solution to the k-center problem on the current window with an approximation quality that can be made arbitrarily close to the best approximation attainable by a sequential algorithm running on the entire window. Remarkably, the size of our coreset is independent of the window size and can be upper bounded by a function of k, of the desired accuracy, and of the doubling dimension of the metric space induced by the stream. For streams of bounded doubling dimension, the coreset size is merely linear in k. One of the major strengths of our algorithm is that it is fully oblivious to the doubling dimension of the stream, and it adapts to the characteristics of each individual window. Also, unlike previous works, the algorithm can be made oblivious to the aspect ratio of the metric space, a parameter related to the spread of distances. We also provide experimental evidence of the practical viability of the approach and its superiority over the current state of the art.

### Running the project
To compile and run the tests use

	javac src\test\Main.java
	java src.test.Main

### References

<a id="1">[1]</a>
P. Pellizzoni, A. Pietracaprina and G. Pucci, *"Dimensionality-adaptive k-center in sliding windows"* 2020 IEEE 7th International Conference on Data Science and Advanced Analytics (DSAA), sydney, Australia, 2020, pp. 197-206, doi: 10.1109/DSAA49011.2020.00032.
