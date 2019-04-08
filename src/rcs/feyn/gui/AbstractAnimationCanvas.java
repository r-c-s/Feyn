package rcs.feyn.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import rcs.feyn.event.DeltaMouseHandler;
import rcs.feyn.event.DeltaMouseListener;
import rcs.feyn.two.gfx.Graphics2d;
import rcs.feyn.utils.ArrayUtils;
import rcs.feyn.utils.TimeUtils;

public abstract class AbstractAnimationCanvas 
    extends DoubleBufferedCanvas implements KeyListener, MouseListener, MouseMotionListener {

  private static final long serialVersionUID = 1L;

  public static final long TARGET_FPS   = 60;
  public static final long FPS_DELAY_MS = 1000 / TARGET_FPS; 

  public static final long TARGET_UPS   = 60;
  public static final long UPS_DELAY_MS = 1000 / TARGET_UPS; 

  protected boolean[] pressed = new boolean[100];  

  protected boolean running = true;
  protected boolean paused  = false;

  private final Thread drawThread = new Thread(() -> {
    while (true) {
      long duration = TimeUtils.getElapsedTimeMillis(() -> { 
        repaintSynchronous();
      });
      
      // FPS:
      // System.out.println(1000.0 / duration);
      
      if (duration < FPS_DELAY_MS) {
        try {
          Thread.sleep(FPS_DELAY_MS - duration);
        }
        catch (final Exception e) { }
      }
    }
  });

  private final Thread mainThread = new Thread(() -> { 
    while (running) { 
      long duration = TimeUtils.getElapsedTimeMillis(() -> {
        updateState();
      }); 
      
      if (duration < UPS_DELAY_MS) {
        try {
          Thread.sleep(UPS_DELAY_MS - duration);
        }
        catch (final Exception e) { }
      }
    }
    System.exit(0);
  }); 

  public AbstractAnimationCanvas(Graphics2d graphics) {
    super(graphics);
  }

  public final void init() {
    initialize(); 
    requestFocus();
    
    mainThread.start(); 
    drawThread.start();
  }

  protected void initialize() { }

  public final void stop() {
    running = false;
  }

  public final void resume() {
    paused = false;
  }

  public final void pause() {
    paused = true;
  }

  public final void togglePause() {
    paused = !paused;
  }

  public void updateState() {
    if (!paused) { 
      runningLoop();
    } else {
      pausedLoop();
    }
  }

  public abstract void runningLoop();

  public abstract void pausedLoop();

  public final void addDeltaMouseListener(DeltaMouseListener dml) { 
    addMouseMotionListener(new DeltaMouseHandler(dml));
  }

  @Override
  public void keyTyped(KeyEvent e) { }

  @Override
  public final void keyPressed(KeyEvent e) {
    keyPressed(e.getKeyCode());
  }

  @Override
  public final void keyReleased(KeyEvent e) {
    keyReleased(e.getKeyCode());
  }

  public void keyPressed(int code) {
    if (code <= pressed.length) {
      if (code == KeyEvent.VK_Q) {
        stop();
      }
      if (code == KeyEvent.VK_P) {
        togglePause();
      }
      pressed[code] = true;
    }
  }

  public void keyReleased(int code) {
    if (code <= pressed.length) {
      pressed[code] = false;
    }
  }

  public int[] getKeysPressed() {
    ArrayList<Integer> keysPressed = new ArrayList<>();
    for (int i = 0; i < pressed.length; i++) {
      if (pressed[i]) {
        keysPressed.add(i);
      }
    } 
    return ArrayUtils.unbox(keysPressed.toArray(new Integer[]{}));
  }

  @Override
  public void mouseDragged(MouseEvent e) { }

  @Override
  public void mouseMoved(MouseEvent e) { } 

  @Override
  public void mouseClicked(MouseEvent e) { }

  @Override
  public void mouseEntered(MouseEvent e) { }

  @Override
  public void mouseExited(MouseEvent e) { }

  @Override
  public void mousePressed(MouseEvent e) { }

  @Override
  public void mouseReleased(MouseEvent e) { }
}
