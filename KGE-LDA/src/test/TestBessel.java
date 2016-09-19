package test;

import org.apache.commons.math3.distribution.LogNormalDistribution;

import jdistlib.math.Bessel;
import util.Common;

public class TestBessel {

	public static void main(String[] args) {

		long time = System.currentTimeMillis();

		double x = Bessel.i(1.51, 24, false);

		long end = System.currentTimeMillis() - time;

		System.out.println(end);

		System.out.println(x);

		time = System.currentTimeMillis();

		LogNormalDistribution lnd = new LogNormalDistribution(2, 0.5);

		double y = lnd.sample();

		double[] z = lnd.sample(1000);

		for (double e : z) {

			System.out.println(e + "\t" + lnd.density(e));

		}

		end = System.currentTimeMillis() - time;

		System.out.println(end);

		System.out.println(y);

		double c = Common.C(y, 50);
		System.out.println(c);

	}

}
