package com.yinong.kilobolt;

public class Test {

	public static void main(String[] args) {
		BouncingBall ball = new BouncingBall();
		
		ball.setHeight(100);
		ball.setY(0);
		
		for(int i=0;i<1000;i++) {
			System.out.println(ball.getY());
			ball.update();
		}
		
	}
}
