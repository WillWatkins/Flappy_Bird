package com.williamwatkins.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.GenericDeclaration;
import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;

	///Texture is an image
	//Background variables
	Texture background;
	Texture gameOver;

	//Bird variables
	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;

	//General game variables
	int gameState = 0;
	float gravity = 2;

	//Tube variables
	Texture topTube;
	Texture bottomTube;
	Float gap = Float.valueOf(400);
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	//Collision detection variables
	//ShapeRenderer is similar to a texture, except shapes allow us to do collision detection
	//ShapeRenderer shapeRenderer;
	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	//Score tracker
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	@Override
	public void create() {
		//Background
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("flappybird_game_over.png");

		//Bird
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");


		//Tubes
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() /2;

		//Collision detection elements
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		//Scoring
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();
	}

	public void startGame(){

		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++) {

			//Sets the up the tubes, distance between tubes and the randomisation of the tubes location
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}


	@Override
	public void render() {

		//Batches just draw textures
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//If the game is started, this code is executed
		if (gameState == 1) {

			if(tubeX[scoringTube] < Gdx.graphics.getWidth() /2 ) {
				score++;

				Gdx.app.log("Score", String.valueOf(score));
				if (scoringTube < numberOfTubes - 1) {
					scoringTube++;

				}
				else {
					scoringTube = 0;
				}
			}

			if (Gdx.input.justTouched()) {

				//Moves the bird down
				velocity = -30;
			}

			for (int i = 0; i < numberOfTubes; i++) {

				//Resets the tubes to the right of the screen from once they leave the left of the screen
				//with a new randomisation of tube heights.
				if (tubeX[i] < -topTube.getWidth()) {

					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {
					//Moves the tubes from right to left.
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}


			if (birdY > 0) {

				velocity = velocity + gravity;
				birdY -= velocity;
			}
			else {
				gameState = 2;
			}
		} else if (gameState == 0) {

			if (Gdx.input.justTouched()) {

				//Starts the game
				gameState = 1;
			}
		} else if (gameState == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2  - gameOver.getHeight());

			if (Gdx.input.justTouched()) {

				//Starts the game
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		//Makes the bird 'flap' it's wings.
		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

		//Score
		font.draw(batch, String.valueOf(score), 100,200);

		//.set (centre of bird, height of bird and width of bird.
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

		//Collision detection elements
		//This was used for testing to be able to visualise the elements that were created to test for detection
		//Once it was proven to work, the code was commented out.
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);


		for (int i = 0; i < numberOfTubes; i++) {
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){

				gameState = 2;

			}
		}

		//shapeRenderer.end();
		batch.end();
	}
}
