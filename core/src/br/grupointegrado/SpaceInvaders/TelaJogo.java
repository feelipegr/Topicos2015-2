package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class TelaJogo extends TelaBase {

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage palco;
	private Stage palcoInformacoes;
	private BitmapFont fonte;
	private Label lbPontuacao;
	private Label lbGameOver;
	private Image jogador;
	private Texture texturaJogador;
	private Texture texturaJogadorDireita;
	private Texture texturaJogadorEsquerda;
	private boolean indoDireita;
	private boolean indoEsquerda;
	private boolean atirando;
	private Array<Image> tiros = new Array<Image>();
	private Texture texturaTiro;
	private Texture texturaMeteoro1;
	private Texture texturaMeteoro2;
	private Array<Image> meteoros1 = new Array<Image>();
	private Array<Image> meteoros2 = new Array<Image>();

	private Array<Texture> texturasExplosao = new Array<Texture>();
	private Array<Explosao> explosoes = new Array<Explosao>();

	private Sound somTiro;
	private Sound somExplosao;
	private Sound somGameOver;
	private Music musicaFundo;


	/**
	  * Construtor padrao da tela do Jogo
	 * @param game Referencia para a classe principal.
	 */
	public TelaJogo(MainGame game) {
		super(game);
	}

	/**
	 *  Chamado quando a tela e exibida.
	 */
	@Override
	public void show() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
		palcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

		initSons();
		initTexturas();
		initFonte();
		initInformacoes();
		initJogador();
	}

	private void initSons() {
		somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
		somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
		somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
		musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
		musicaFundo.setLooping(true);
	}

	private void initTexturas() {
		texturaTiro = new Texture("sprites/shot.png");
		texturaMeteoro1 = new Texture("sprites/enemie-1.png");
		texturaMeteoro2 = new Texture("sprites/enemie-2.png");

		for (int i = 1; i <= 17; i++) {
			Texture text = new Texture("sprites/explosion-"+i+".png");
			texturasExplosao.add(text);
		}
	}

	/**
	 * Instancia o objeto jogador no palco.
	 */

	private void initJogador() {
		texturaJogador = new Texture("sprites/player.png");
		texturaJogadorDireita = new Texture("sprites/player-right.png");
		texturaJogadorEsquerda = new Texture("sprites/player-left.png");

		jogador = new Image(texturaJogador);
		float x = camera.viewportWidth/2 - jogador.getWidth() /2;
		float y = 15;
		jogador.setPosition(x, y);
		palco.addActor(jogador);
	}

	/**
	 * Instancia as informacoes escritas na tela.
	 */

	private void initInformacoes() {
		Label.LabelStyle lbEstilo = new Label.LabelStyle();
		lbEstilo.font = fonte;
		lbEstilo.fontColor = Color.WHITE;

		lbPontuacao = new Label("0 pontos", lbEstilo);
		palcoInformacoes.addActor(lbPontuacao);

		lbGameOver = new Label("Game Over!", lbEstilo);
		lbGameOver.setVisible(false);
		palcoInformacoes.addActor(lbGameOver);
	}

	/**
	 * Instancia os objetos da Fonte.
	 */

	private void initFonte() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.color = Color.WHITE;
		param.size = 24;
		param.shadowOffsetX =2;
		param.shadowOffsetY = 2;
		param.shadowColor = Color.BLUE;

		fonte = generator.generateFont(param);

		generator.dispose();

	}

	/**
	 * Chamado a todo quadro de atualizacao do jogo. (FPS)
	 * @param delta Tempo entre um quadro e outro (em segundos).
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getPrefHeight() - 10); // X Y viewportHeight-20 significa que ira seta na posição a 20 pixel abaixo da alyura maxima da tela.
		lbPontuacao.setText(pontuacao + " Pontos");

		lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getPrefWidth() / 2, camera.viewportHeight / 2);

		lbGameOver.setVisible(gameOver == true);

		atualizarExplosoes(delta);
		if (gameOver == false) {
			if (!musicaFundo.isPlaying()) // se não está tocando
				musicaFundo.play();
			capturaTeclas();
			atualizarJogador(delta);
			atualizarTiros(delta);
			atualizarMeteoros(delta);
			detectarColisoes(meteoros1, 5);
			detectarColisoes(meteoros2, 15);
		} else {
			if (musicaFundo.isPlaying())
				musicaFundo.stop();
		}



		// atualiza a situacao do palco.
		palco.act(delta);
		// desenha o palco na tela.
		palco.draw();

		// desenha o palco de informacoes
		palcoInformacoes.act(delta);
		palcoInformacoes.draw();
	}

	private void atualizarExplosoes(float delta) {
		for (Explosao explosao : explosoes) {
			// verifica se a explosao chegou ao fim
			if (explosao.getEstagio() >= 16) {
				explosoes.removeValue(explosao, true); // remove a explosao do array
				explosao.getAtor().remove(); // remove o ator do palco
			} else {
				// ainda nao chegou ao fim
				explosao.atualizar(delta);
			}
		}
	}

	private Rectangle recJogador = new Rectangle();
	private Rectangle recTiro = new Rectangle();
	private Rectangle recMeteoro = new Rectangle();
	private int pontuacao = 0;
	private boolean gameOver = false;

	private void detectarColisoes(Array<Image> meteoros, int valePonto) {
		recJogador.set(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getHeight());
		for (Image meteoro : meteoros) {
			recMeteoro.set(meteoro.getX(), meteoro.getY(), meteoro.getWidth(), meteoro.getHeight());
			// detecta colisoes com os tiros
			for (Image tiro : tiros) {
				recTiro.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());
				if (recMeteoro.overlaps(recTiro)) {
					// aqui ocorre uma colisão do tiro com o meteoro 1
					pontuacao += valePonto;
					tiro.remove(); // remove do palco
					tiros.removeValue(tiro, true); // remove da lista
					meteoro.remove(); // remove do placo
					meteoros.removeValue(meteoro, true); // remove da lista
					criarExplosao(meteoro.getX() + meteoro.getWidth(), meteoro.getY() + meteoro.getHeight() / 2);
					somExplosao.play();
				}
			}
			// detecta colisao com o player
			if (recJogador.overlaps(recMeteoro)) {
				gameOver = true;
				somGameOver.play();
				musicaFundo.stop();
			}
		}
	}

	/**
	 * Cria a explosao na posicao X e Y.
	 * @param x
	 * @param y
	 */

	private void criarExplosao(float x, float y) {
		Image ator = new Image(texturasExplosao.get(0));
		ator.setPosition(x - ator.getWidth() / 2, y - ator.getHeight() / 2);
		palco.addActor(ator);

		Explosao explosao = new Explosao(ator, texturasExplosao);
		explosoes.add(explosao);
	}

	private void atualizarMeteoros(float delta) {
		int qtdMeteoros = meteoros1.size + meteoros2.size;

		if (qtdMeteoros < 15) {
			int tipo = MathUtils.random(1, 4); // retorna um ou dois aleatoriamente
			if (tipo == 1) {
				// cria meteoro 1
				Image meteoro = new Image(texturaMeteoro1);
				float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
				float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
				meteoro.setPosition(x, y);
				meteoros1.add(meteoro);
				palco.addActor(meteoro);
			} else if (tipo == 2) {
				Image meteoro = new Image(texturaMeteoro2);
				float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
				float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
				meteoro.setPosition(x, y);
				meteoros2.add(meteoro);
				palco.addActor(meteoro);
			}
		}
			float velocidade = 100; // pixels por segundo
			for (Image meteoro : meteoros1) {
				float x = meteoro.getX();
				float y = meteoro.getY() - velocidade * delta;
				meteoro.setPosition(x, y);
				if (meteoro.getY() + meteoro.getHeight() < 0) {
					meteoro.remove(); // remove do palco
					meteoros1.removeValue(meteoro, true); //remove da lista
				}
			}

			float velocidade2 = 150; // pixels por segundo
			for (Image meteoro : meteoros2) {
				float x = meteoro.getX();
				float y = meteoro.getY() - velocidade2 * delta;
				meteoro.setPosition(x, y);
				if (meteoro.getY() + meteoro.getHeight() < 0) {
					meteoro.remove(); // remove do palco
					meteoros2.removeValue(meteoro, true); //remove da lista
				}
			}
	}

	private final float MIN_INTERVALO_TIROS = 0.4f; // minimo de tempo entre os tiros
	private float intervaloTiros = 0; // tempo acumulado entre os tiros

	private void atualizarTiros(float delta) {
		intervaloTiros = intervaloTiros + delta; // acumula o tempo percorrido
		// cria um novo tiro se necessario
		if (atirando) {
			// verifica se o tempo minimo foi atingido
			if (intervaloTiros >= MIN_INTERVALO_TIROS) {
				Image tiro = new Image(texturaTiro);
				float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
				float y = jogador.getY() + jogador.getHeight();
				tiro.setPosition(x, y);
				tiros.add(tiro);
				palco.addActor(tiro);
				intervaloTiros = 0;
				somTiro.play();
			}
		}
		float velocidade = 200; // velocidade de movimentacao do tiro
		// percorre todos os tiros existentes
		for (Image tiro : tiros) {
			// movimenta o tiro em direcao ao topo
			float x = tiro.getX();
			float y = tiro.getY() + velocidade * delta;
			tiro.setPosition(x, y);
			// remove os tiros que sairam da tela
			if (tiro.getY() > camera.viewportHeight) {
				tiros.removeValue(tiro, true); // remove da lista
				tiro.remove(); // remove do palco
			}
		}
	}

	/**
	 * Atualiza a posicao do jogador
	 * @param delta
	 */

	private void atualizarJogador(float delta) {
		float velocidade = 200; // velocidade de movimento do jogador
		if (indoDireita) {
			// verifica se o jogador está dentro da tela.
			if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
				float x = jogador.getX() + velocidade * delta;
				float y = jogador.getY();
				jogador.setPosition(x, y);
			}
		}

		if (indoEsquerda) {
			// verifica se o jogador está dentro da tela.
			if (jogador.getX() > 0) {
				float x = jogador.getX() - velocidade * delta;
				float y = jogador.getY();
				jogador.setPosition(x, y);
			}

		}

		if (indoDireita) {
			jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorDireita)));
		} else if (indoEsquerda) {
			jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorEsquerda)));
		} else {
			jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogador)));
		}
	}

	/**
	 * Verifica se as teclas estao pressionadas.
	 */

	private void capturaTeclas() {
		indoDireita = false;
		indoEsquerda = false;
		atirando = false;

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			indoEsquerda = true;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			indoDireita = true;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			atirando = true;
		}
	}

	/**
	 * E chamado sempre que ha uma alteracao no tamanho da tela.
	 * @param width Novo valor de largura da tela.
	 * @param heigth Novo valor de alteura da tela.
	 */

	@Override
	public void resize(int width, int heigth) {
		camera.setToOrtho(false, width, heigth);
		camera.update();
	}

	/**
	 * � chamado sempre que o jogo for minimizado.
	 */

	@Override
	public void pause() {

	}

	/**
	 * � chamado sempre que o jogo voltar para o primeiro plano.
	 */

	@Override
	public void resume() {

	}

	/**
	 * � chamado quando a tela for destru�da.
	 */

	@Override
	public void dispose() {
		batch.dispose();
		palco.dispose();
		palcoInformacoes.dispose();
		fonte.dispose();
		texturaJogador.dispose();
		texturaJogadorEsquerda.dispose();
		texturaJogadorDireita.dispose();
		texturaTiro.dispose();
		texturaMeteoro1.dispose();
		texturaMeteoro2.dispose();
		for (Texture text : texturasExplosao) {
			text.dispose();
		}
		somTiro.dispose();
		somExplosao.dispose();
		somGameOver.dispose();
		musicaFundo.dispose();
	}
}
