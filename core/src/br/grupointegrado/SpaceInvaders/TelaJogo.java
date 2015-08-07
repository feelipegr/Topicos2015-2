package br.grupointegrado.SpaceInvaders;

import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class TelaJogo extends TelaBase {

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage palco;
	private BitmapFont fonte;
	private Label lbPontuacao;

	/**
	  * Construtor padr�o da tela do Jogo
	 * @param game Refer�ncia para a classe principal.
	 */
	public TelaJogo(MainGame game) {
		super(game);
	}

	/**
	 *  Chamado quando a tela � exibida.
	 */
	@Override
	public void show() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

		initFonte();
		initInformacoes();
	}

	private void initInformacoes() {
		Label.LabelStyle lbEstilo = new Label.LabelStyle();
		lbEstilo.font = fonte;
		lbEstilo.fontColor = Color.WHITE;

		lbPontuacao = new Label("0 pontos", lbEstilo);
		palco.addActor(lbPontuacao);
	}

	private void initFonte() {
		fonte = new BitmapFont();
	}

	/**
	 * Chamado a todo quadro de atualiza��o do jogo. (FPS)
	 * @param delta Tempo entre um quadro e outro (em segundos).
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
		Gdx.gl.glClearColor(GL20.GL_COLOR_BUFFER_BIT);

		palco.act(delta);
		palco.draw();
	}

	/**
	 * � chamado sempre que h� uma altera��o no tamanho da tela.
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
		fonte.dispose();
	}
}
