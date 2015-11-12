package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;
import sun.invoke.empty.Empty;

/**
 * test
 * @author Paidarakis Konstantinos
 */
public class Main extends SimpleApplication implements ActionListener,AnimEventListener{
    
    private BulletAppState bulletAppState;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        
    }
    
    private int  displacement2  = 0;
    private int displacement = 0;
    private int credit = 0;
    private int stamina = 5000;
    private int cannonballs, bananas, mines,explosiveMines, food = 0;
    private Spatial sceneModel, sceneModel2, teapot;
    public Spatial ex;
    private RigidBodyControl landscape, landscape2;
    private CharacterControl character, vendor, cassio, monkey;
    private Node model, model2, model3, model4, collectables, vaultNode, explosives;
    private ChaseCamera chaseCam;
    private boolean left,right,up,down, level;
    private boolean key, torch, pursuit, monkeyOnMe, MonEating, fireon, gameOver = false;
    private boolean awayFromCassio, away, awayFromMonkey = true;
    Vector3f walkDirection = new Vector3f();
    private float airTime, vis2, vis3, vis4, timer, timer2, eatingTimer;
    private AnimControl animationControl,animationControl2, animationControl3, animationControl4;
    private AnimChannel animationChannel, animationChannel2, animationChannel3, animationChannel4;
    private Vector3f otoLocation, sinbadLocation, Oto2SinBad, cassioLocation, monkeyLocation, Oto2cassio, Oto2monkey, walkMonkey;
    DirectionalLight dl;
    PointLight pl;
    BitmapText hudText, hudTextVendor, hudTextCassio, hudTextMonkey, hudTextinfo, hudTextGameOver, hudTextSTWarning ,hudTextWin,hudTextCannons;
    ParticleEmitter fire,explosion;
    CollisionResult closest2, closest;
    CollisionResults results, results2;
    private MotionPath path;
    private MotionEvent motionControl;
    float PROXIMITY = 4.0f;
    private Node sceneNode;
    RigidBodyControl mine_phy;
    
    @Override
    public void simpleInitApp() {
       bulletAppState = new BulletAppState();
       stateManager.attach(bulletAppState);
       
       setDisplayFps(false);
       setDisplayStatView(false);
       
       
            hudText = new BitmapText(guiFont, false);        
            hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
            hudText.setColor(ColorRGBA.White);// font color
        //    String text1=hudText.getText();
       //     String text2[]=text1.split(":");
            
//          hudText.setText("STAMINA: "+ stamina);             // the text
            hudText.setLocalTranslation(settings.getWidth()/1000f, settings.getHeight()/1.9f, 0); // position
            guiNode.attachChild(hudText);
            
            hudTextVendor = new BitmapText(guiFont, false);
            hudTextVendor.setSize(guiFont.getCharSet().getRenderedSize());
            hudTextVendor.setColor(ColorRGBA.Yellow);
            
            hudTextVendor.setLocalTranslation(settings.getWidth()/2.7f, settings.getHeight()/1.15f, 0);
            guiNode.attachChild(hudTextVendor);
            
            hudTextCassio = new BitmapText(guiFont, false);
            hudTextCassio.setSize(guiFont.getCharSet().getRenderedSize());
            hudTextCassio.setColor(ColorRGBA.Yellow);
            
            hudTextCassio.setLocalTranslation(settings.getWidth()/2.7f, settings.getHeight()/1.15f, 0);
            guiNode.attachChild(hudTextCassio);
            
            hudTextMonkey = new BitmapText(guiFont, false);
            hudTextMonkey.setSize(30);
            hudTextMonkey.setColor(ColorRGBA.Red);
            
            hudTextMonkey.setLocalTranslation(settings.getWidth()/2.4f, settings.getHeight()/1.08f, 0);
            guiNode.attachChild(hudTextMonkey);
            
            hudTextinfo = new BitmapText(guiFont, false);
            hudTextinfo.setSize(guiFont.getCharSet().getRenderedSize());
            hudTextinfo.setColor(ColorRGBA.Yellow);
            hudTextinfo.setLocalTranslation(settings.getWidth()/2.16f, settings.getHeight()/2.8f, 0);
            guiNode.attachChild(hudTextinfo);
            
            hudTextSTWarning = new BitmapText(guiFont, false);
            hudTextSTWarning.setSize(45);
            hudTextSTWarning.setColor(ColorRGBA.Red);
            hudTextSTWarning.setLocalTranslation(settings.getWidth()/1.6f, settings.getHeight()/4f, 0);
            guiNode.attachChild(hudTextSTWarning);
            
            hudTextCannons = new BitmapText(guiFont, false);
            hudTextCannons.setSize(guiFont.getCharSet().getRenderedSize());
            hudTextCannons.setColor(ColorRGBA.Red);
            hudTextCannons.setLocalTranslation(settings.getWidth()/2.16f, settings.getHeight()/3.1f, 0);
            guiNode.attachChild(hudTextCannons);
//             hudText6 = new BitmapText(guiFont, false);
//            hudText6.setSize(guiFont.getCharSet().getRenderedSize());
//            hudText6.setColor(ColorRGBA.Yellow);
//            hudText6.setLocalTranslation(470, hudText2.getLineHeight()+680, 0);
//            guiNode.attachChild(hudText6);
            
        hudTextGameOver = new BitmapText(guiFont, false);        
        hudTextGameOver.setSize(100);      // font size
        hudTextGameOver.setColor(ColorRGBA.Red);                             // font color
        hudTextGameOver.setLocalTranslation(settings.getWidth()/3.1f, settings.getHeight()/1.4f, 0); // position
        guiNode.attachChild(hudTextGameOver);
        
        hudTextWin = new BitmapText(guiFont, false);        
        hudTextWin.setSize(100);      // font size
        hudTextWin.setColor(ColorRGBA.Green);                             // font color
        hudTextWin.setLocalTranslation(settings.getWidth()/3.1f, settings.getHeight()/1.4f, 0); // position
        guiNode.attachChild(hudTextWin);
            
        
        setupKeys();
        createTerrain();
        createSky();
        createLight();
        createCharacter();
        setupChaseCamera();
        createVendor();
        createCassio();
        createMonkey();
        makemines();
        makeExplosiveMines();
        makeInventory();
        makeInventory2();
        makeInventory3();
        createPointLight();
        makewall();
        createDoor();
        //setupMotionPath();
        //motionControl.play();
        
        setupAnimationController();
        
    }

    
    @Override
    public void simpleUpdate(float tpf) {
        
      
        //System.out.println(character.getPhysicsLocation());
        //System.out.println(cam.getDirection());
                
                //clearing the hudTexrInfo
                timer2 += tpf;
                System.out.println(timer2);
                if(timer2 > 2){
                    hudTextinfo.setText("");
                    hudTextCannons.setText("");
                    timer2 = 0;
                }
//             
//            hudText = new BitmapText(guiFont, false);        
//            hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
//            hudText.setColor(new ColorRGBA(0.5f, 0.3f, 0f, 0.5f));                             // font color
//        //    String text1=hudText.getText();
//       //     String text2[]=text1.split(":");
//            
//            hudText.setText("STAMINA: "+ stamina);             // the text
//            hudText.setLocalTranslation(0, settings.getHeight(), 0); // position
//            guiNode.attachChild(hudText);
      if(gameOver == false) {
            if(character.getPhysicsLocation().y > -5 && character.getPhysicsLocation().y < 0) {
              if(character.getPhysicsLocation().z<-110 && character.getPhysicsLocation().x>40 && character.getPhysicsLocation().x<100){
                    level = true;
                    character.setPhysicsLocation(new Vector3f(40,10,0));
                    cassio.setPhysicsLocation(new Vector3f(5.06524f, 1, -12.185619f));
                    rootNode.attachChild(model3);
                    monkey.setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-31,72), 2.01976f, FastMath.nextRandomInt(-9,-9)));
                    rootNode.attachChild(model4);
                    rootNode.attachChild(explosives);
                    rootNode.detachChild(collectables);
                    
                    
                }else 
                  gameOver = true;
            }
        pl.setPosition(character.getPhysicsLocation());
        
        hudText.setText("\n\nCREDITS: "+credit+"\n\nBANANAS: "+bananas+
                "\n\nCANNONBALLS: "+cannonballs+"\n\nFOOD: "+food+ 
                "\n\nSTAMINA: "+ stamina+ "\n\nMines: "+mines+
                "\n\nExplosives "+explosiveMines);
        
        stamina -= tpf;
        stamina();
        
        // Game Over when Stamina reaches 0.
        if(stamina <= 0){
            stamina = 0;
            rootNode.detachAllChildren();;
           hudTextGameOver.setText("You died!\nGame Over");
        }
        // Warning message when stamina below 800!
        else if(stamina <= 800){
           hudTextSTWarning.setText("Warning!\nstamina low\nbuy some food!");
        } else if (stamina >= 800) {
           hudTextSTWarning.setText("");
        }
        
        if(mines >= 10 && explosiveMines >= 10 && gameOver == false) {
            hudTextWin.setText("You win!");
        }
        
       
         Vector3f camDir = cam.getDirection().clone().multLocal(0.5f); //speed
         Vector3f camLeft = cam.getLeft().clone().multLocal(0.5f);
        
        timer += tpf;
        if (timer > 40) {
        timer=0;
        }
       // System.out.println(timer);
        if (timer > 10 && timer < 20 || timer > 30) {
         rootNode.removeLight(dl);
        // System.out.println(rootNode.);
         afternoonLight();
         //camDir = cam.getDirection().clone().multLocal(0.2f); //speed
         //camLeft = cam.getLeft().clone().multLocal(0.2f);
        } else if (timer > 20 && timer <= 30) {
         rootNode.removeLight(dl);
         nightLight();
         //camDir = cam.getDirection().clone().multLocal(0.1f); //speed
         //camLeft = cam.getLeft().clone().multLocal(0.1f);
        }else if (timer < 10) {
         rootNode.removeLight(dl);
         createLight();
        }
        
//        hudText = new BitmapText(guiFont,false);
//        hudText.setSize(guiFont.getCharSet().getRenderedSize());
//        hudText.setColor(ColorRGBA.Yellow);
//        hudText.setText("stamina");
//        hudText.setLocalTranslation(300, hudText.getLineHeight(), 0);
//        guiNode.attachChild(hudText);
        
        camDir.y = 0;
        camLeft.y = 0;
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        
        
        if (!character.onGround()) {
            airTime = airTime + tpf;
        } else {
            airTime = 0;
        }
        if (walkDirection.length() == 0) {
            if (!"stand".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("stand", 1f);
            }
        } else {
            character.setViewDirection(walkDirection);
            if (airTime > .3f) {
                if (!"stand".equals(animationChannel.getAnimationName())) {
                    animationChannel.setAnim("stand");
                }
            } else if (!"Walk".equals(animationChannel.getAnimationName())) {
                animationChannel.setAnim("Walk", 0.2f);
            }
        }

        character.setWalkDirection(walkDirection);
        
        otoLocation = character.getPhysicsLocation();
        sinbadLocation = vendor.getPhysicsLocation();
        Oto2SinBad = sinbadLocation.subtract(otoLocation).multLocal(0.1f);
        
        Vector3f vD = new Vector3f();
        vD = vendor.getViewDirection().mult(-1f);
        vis2 = (Oto2SinBad.normalize()).dot((vD.normalize()));
        
       
        if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)* FastMath.RAD_TO_DEG) < 60 && away == true) {
             away = false;
            animationChannel2.setAnim("SliceVertical");
            hudTextVendor.setText("Hello Stranger!!! Press 1 to buy a torch so you can see in the night!\n"
                    + "Press 2 to get the key for the door\n"
                    + "or 3 to buy some food!");
            
            //System.out.println("Hello Stranger!!! Please press 1 to buy a torch!");
           }
        if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation()) >= 10 || (Math.acos(vis2)* FastMath.RAD_TO_DEG) > 60) {
           away = true;
           hudTextVendor.setText("");
        }
        
        cassioLocation = cassio.getPhysicsLocation();
        Oto2cassio = cassioLocation.subtract(otoLocation).multLocal(0.1f);
        
        Vector3f vD2 = new Vector3f();
        vD2 = cassio.getViewDirection().mult(-1f);
        vis3 = (Oto2cassio.normalize()).dot((vD2.normalize()));
        
       
        if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) < 10 && (Math.acos(vis3)* FastMath.RAD_TO_DEG) < 60 && awayFromCassio == true) {
             awayFromCassio = false;
            animationChannel3.setAnim("SliceVertical");
            hudTextCassio.setText("Hello im cassio!!! Please press 1 to buy bananas!\n"
                    + "or 2 to buy cannonballs!");
            //System.out.println("Hello im cassio!!! Please press 1 to buy bananas!");
            
        }
        if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) >= 10 || (Math.acos(vis3)* FastMath.RAD_TO_DEG) > 60) {
           awayFromCassio = true;
           hudTextCassio.setText("");
        }
        
        monkeyLocation = monkey.getPhysicsLocation();
        Oto2monkey = monkeyLocation.subtract(otoLocation).multLocal(0.1f);
        
        Vector3f vD3 = new Vector3f();
        vD3 = monkey.getViewDirection();
        vis4 = (Oto2monkey.normalize()).dot((vD3.normalize()));
        
       
        if(character.getPhysicsLocation().distance(monkey.getPhysicsLocation()) < 80 && (Math.acos(vis4)* FastMath.RAD_TO_DEG) < 120 && awayFromMonkey == true && level == true) { 
            //awayFromMonkey = false;
            monkeyOnMe = true;
            pursuit = true;
            animationChannel4.setAnim("Walk");
            hudTextMonkey.setText("Haha i got you!!!");
            //System.out.println("Haha i got you!!!");
            //motionControl.stop();
            
            if (pursuit) {
            animationChannel4.setAnim("Walk");
//            monkeyLocation = monkey.getPhysicsLocation();
//            otoLocation = character.getPhysicsLocation();
//            monkey.setViewDirection(otoLocation); //to use with physic based characters

            if (monkeyLocation.distance(otoLocation) < PROXIMITY) {
                if(MonEating ==  false) {
                    stamina -= 20;
                    System.out.println("hit!");
                }
                walkMonkey = new Vector3f(0f, 0f, 0f);
                pursuit = false;
                //animationChannel4.setAnim("Idle");
            } else {
                walkMonkey = otoLocation.subtract(monkeyLocation).multLocal(0.01f);
                monkey.setViewDirection(walkMonkey.mult(-1f));
                animationChannel4.setAnim("Walk");
              }
            }
            
            monkey.setWalkDirection(walkMonkey);
      }else {
            hudTextMonkey.setText("");
        }
        
        if(level == false) {
          //results = new CollisionResults();
          //Ray ray = new Ray(character.getPhysicsLocation(), character.getViewDirection());
          //collectables.collideWith(ray, results);
          
          
//                    if (results.size() > 0) {
          //closest = results.getClosestCollision();
//          float distance = closest.getDistance();
//            if(distance < 15) {
                
               for(Spatial col:collectables.getChildren()) {
                    if(character.getPhysicsLocation().distance(col.getLocalTranslation()) < 5) {
                     animationChannel.setAnim("Dodge",2.1f);   
                    collectables.detachChild(col);
                    
                    
               
                hudTextinfo.setText("Mine Collected");
                System.out.println("Mine collected");
                mines++;
                System.out.println(mines);
                //collectables.detachChild(closest.getGeometry());
                createMineImg();
                //displacement += 30;
//            Box guiBox = new Box(new Vector3f(0f,0f,0f),1,1,1);
//            Geometry geoGuiBox;
//            geoGuiBox = new Geometry("Inventory Cube",guiBox);
//             Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            mat1.setColor("Color",ColorRGBA.Blue);
//            geoGuiBox.setMaterial(mat1);
//            
//             guiNode.attachChild(geoGuiBox);
//             geoGuiBox.setLocalScale(10);
//             geoGuiBox.setLocalTranslation(settings.getWidth()-21,displacement,0);
//                displacement+=30;
                
                credit += 10;
                System.out.println("credits : " + credit);
                
//                if () {
//                timer2 += tpf;
//                System.out.println(timer2);
//                if(timer2 > 2){
//                    hudTextinfo.setText("");
//                    timer2 = 0;
//                }
//             }
                
                
            //}
//         }else {
//                timer2 += tpf;
//                System.out.println(timer2);
//                if(timer2 > 2){
//                    hudTextinfo.setText("");
//                    timer2 = 0;
//                }
//             }
        }
               }
        }
        if(level == true) {            
          results2 = new CollisionResults();
          Ray ray2 = new Ray(character.getPhysicsLocation(), character.getViewDirection());
          explosives.collideWith(ray2, results2);
          
          
          
          for(Spatial ex:explosives.getChildren()){
            if(character.getPhysicsLocation().distance(ex.getLocalTranslation()) < 5) {
                hudTextinfo.setText("Ouch!!!");
                stamina -= 1000;
                System.out.println("Ouch!!!");
                createExplosion2(ex);
                explosives.detachChild(ex);
            }
          }
                    if (results2.size() > 0) {
          closest2 = results2.getClosestCollision();
          float distance2 = closest2.getDistance();
          
          
            if(distance2 <= 100 && distance2 >= 15){
                hudTextinfo.setText("target on sight.");
                System.out.println("target on sight.");
                  }else hudTextinfo.setText("");
            if(distance2 < 5) {
                hudTextinfo.setText("Ouch!!!");
                stamina -= 1000;
                System.out.println("Ouch!!!");
                createExplosion();
                explosives.detachChild(closest2.getGeometry());
                
//                Box guiBox = new Box(new Vector3f(0f,0f,0f),1,1,1);
//            Geometry geoGuiBox;
//            geoGuiBox = new Geometry("Inventory Cube",guiBox);
//             Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            mat1.setColor("Color",ColorRGBA.Red);
//            geoGuiBox.setMaterial(mat1);
//            
//             guiNode.attachChild(geoGuiBox);
//             geoGuiBox.setLocalScale(10);
//             geoGuiBox.setLocalTranslation(settings.getWidth()-60,displacement2,0);
//                displacement2+=30;
//                
//                credit += 20;
                System.out.println("credits : " + credit);
                
            }
                    }else {
                             timer2 += tpf;
                             System.out.println(timer2);
                             if(timer2 > 2){
                             hudTextinfo.setText("");
                             timer2 = 0;
                            }
                         }
            
                    if(MonEating == true) {
                        eatingTimer += tpf;
                        System.out.println(eatingTimer);
                        if(eatingTimer > 10) {
                            MonEating = false;
                            eatingTimer = 0;
                        }
                    }
                    
        }
       
      }else {
          hudTextGameOver.setText("You died!\nGame Over");
          rootNode.detachAllChildren();
      }
          
        
}

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    public void stamina(){
        
        Box initialBox = new Box(new Vector3f(0f,0f,0f),20,1,1);
            
            float width = settings.getWidth();
            Geometry initGuiBox=new Geometry("Initial Cube",initialBox);
            Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat1.setColor("Color", ColorRGBA.Red);
            initGuiBox.setMaterial(mat1);
             guiNode.attachChild(initGuiBox);
             initGuiBox.setLocalScale(10);
             initGuiBox.setLocalTranslation(0,50,0);
       
        Box staminaBox = new Box(new Vector3f(0f,0f,0f),(5000-stamina)/250,1,1);
            Geometry stamGuiBox=new Geometry("Stamina Cube",staminaBox);
             Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat2.setColor("Color", ColorRGBA.White);
               stamGuiBox.setMaterial(mat2);
               guiNode.attachChild(stamGuiBox);
              
             stamGuiBox.setLocalScale(10);
             stamGuiBox.setLocalTranslation(0,50,0);
     }
    
    private void createTerrain() {
        sceneModel = assetManager.loadModel("Scenes/town/main.scene");
        sceneModel.setLocalScale(1f);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        getPhysicsSpace().add(sceneModel);
        
        rootNode.attachChild(sceneModel);
    }
    
    private void createKey() {
        Picture pic = new Picture("Key Picture");
        pic.setImage(assetManager, "Textures/key/key_1_.png", true);
        pic.setWidth(settings.getWidth()/25);
        pic.setHeight(settings.getHeight()/25);
        pic.setPosition(settings.getWidth()/1.2f, settings.getHeight()/11f);
        guiNode.attachChild(pic);

    }
    
    private void createMineImg() {
        Picture pic = new Picture("Mine Picture");
        pic.setImage(assetManager, "Textures/Mines/mine.png", true);
        pic.setWidth(settings.getWidth()/30);
        pic.setHeight(settings.getHeight()/23);
        pic.setPosition(settings.getWidth()/1.079f, settings.getHeight()/50f+displacement);
        displacement += 35;
        guiNode.attachChild(pic);
//settings.getHeight()/50f
    }
    
    private void createExplMineImg() {
        Picture pic = new Picture("Explosive mine Picture");
        pic.setImage(assetManager, "Textures/Mines/ex_mine.png", true);
        pic.setWidth(settings.getWidth()/20);
        pic.setHeight(settings.getHeight()/25);
        pic.setPosition(settings.getWidth()/1.045f, settings.getHeight()/50f+displacement2);
        displacement2 += 35;
        guiNode.attachChild(pic);

    }
    
    private void createSky() {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
    }
    
    private void createLight() {
        Vector3f direction = new Vector3f(-0.1f, -0.7f, -1).normalizeLocal();
        dl = new DirectionalLight();
        dl.setDirection(direction);
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);
    }
    
    private void createPointLight() {
        pl = new PointLight();
        pl.setRadius(30);
        pl.setColor(ColorRGBA.Yellow);
    }
    
    private void nightLight() {
         Vector3f direction = new Vector3f(0.5348667f, -0.6787754f, -0.50317144f).normalizeLocal();
         dl = new DirectionalLight();
         dl.setDirection(direction);
         ColorRGBA nightColor = new ColorRGBA(.06f, .06f, .2f, 1);
         dl.setColor(nightColor);
         rootNode.addLight(dl);
        }
     private void afternoonLight() {
         Vector3f direction = new Vector3f(0.5348667f, -0.6787754f, -0.50317144f).normalizeLocal();
         dl = new DirectionalLight();
         dl.setDirection(direction);
         ColorRGBA nightColor = new ColorRGBA(.06f, .06f, .2f, 1);
         dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f).multLocal(0.6f));
         rootNode.addLight(dl);
     }
    
    private void createCharacter() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 2f, 1);
        character = new CharacterControl(capsule, 2.75f);
        model = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        model.setLocalScale(0.4f);
        character.setJumpSpeed(30f);
        character.setGravity(90f);
        character.setFallSpeed(50f);
        model.addControl(character);
        character.setPhysicsLocation(new Vector3f(35, 1, -10));
        cam.setLocation(character.getPhysicsLocation());
        rootNode.attachChild(model);
        getPhysicsSpace().add(character);
    }
    
    private void createTorch() {
        teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material mat_default = new Material( assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(mat_default);
        teapot.setLocalTranslation(-4, 3f, 1);
        model.attachChild(teapot);
    }
    
    private void createCassio() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1.6f, 1);
        cassio = new CharacterControl(capsule, 2.75f);
        model3 = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        model3.setLocalScale(0.4f);
        model3.addControl(cassio);
//        cassio.setPhysicsLocation(new Vector3f(5.06524f, 1, -12.185619f));
        //rootNode.attachChild(model3);
        getPhysicsSpace().add(cassio);
    }
    
    public void createFire(){
    fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
    Material mat_red = new Material(assetManager, 
            "Common/MatDefs/Misc/Particle.j3md");
    mat_red.setTexture("Texture", assetManager.loadTexture(
            "Effects/Explosion/flame.png"));
    fire.setMaterial(mat_red);
    fire.setImagesX(2); 
    fire.setImagesY(2); // 2x2 texture animation
    fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
    fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
    fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
    fire.setStartSize(1.5f);
    fire.setEndSize(0.1f);
    fire.setGravity(0, 0, 0);
    fire.setLowLife(1f);
    fire.setHighLife(3f);
    fire.getParticleInfluencer().setVelocityVariation(0.3f);
    model.attachChild(fire);
    //rootNode.attachChild(fire);
    }
    
    public void createExplosion(){
    explosion = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
    Material mat_red = new Material(assetManager, 
            "Common/MatDefs/Misc/Particle.j3md");
    mat_red.setTexture("Texture", assetManager.loadTexture(
            "Effects/Explosion/flame.png"));
    explosion.setMaterial(mat_red);
    explosion.setImagesX(2); 
    explosion.setImagesY(2); // 2x2 texture animation
    explosion.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
    explosion.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
    explosion.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
    explosion.setStartSize(0.5f);
    explosion.setEndSize(0.2f);
    explosion.setGravity(0, 0, 0);
    explosion.setLowLife(1f);
    explosion.setHighLife(3f);
    explosion.getParticleInfluencer().setVelocityVariation(0.3f);
    explosion.setLocalTranslation(closest2.getGeometry().getLocalTranslation());
    rootNode.attachChild(explosion);
    }
    
    public void createExplosion2(Spatial ex){
    explosion = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
    Material mat_red = new Material(assetManager, 
            "Common/MatDefs/Misc/Particle.j3md");
    mat_red.setTexture("Texture", assetManager.loadTexture(
            "Effects/Explosion/flame.png"));
    explosion.setMaterial(mat_red);
    explosion.setImagesX(2); 
    explosion.setImagesY(2); // 2x2 texture animation
    explosion.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
    explosion.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
    explosion.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
    explosion.setStartSize(0.5f);
    explosion.setEndSize(0.2f);
    explosion.setGravity(0, 0, 0);
    explosion.setLowLife(1f);
    explosion.setHighLife(3f);
    explosion.getParticleInfluencer().setVelocityVariation(0.3f);
    explosion.setLocalTranslation(ex.getLocalTranslation());
    rootNode.attachChild(explosion);
    }
    
    private void createVendor() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1.6f, 1);
        vendor = new CharacterControl(capsule, 2.75f);
        model2 = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        model2.setLocalScale(0.4f);
        model2.addControl(vendor);
        vendor.setPhysicsLocation(new Vector3f(-6.246029f, 3.001349f, 10.4667f));
        rootNode.attachChild(model2);
        getPhysicsSpace().add(vendor);
    }
    
    private void createMonkey() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.6f, 0.6f);
        monkey = new CharacterControl(capsule, 2.75f);
        model4 = (Node) assetManager.loadModel("Models/monkeyExport/Jaime.j3o");
        model4.setLocalScale(6f);
        model4.addControl(monkey);
        //monkey.setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-70,200), 2.01976f, FastMath.nextRandomInt(-100,75)));
        //rootNode.attachChild(model4);
        getPhysicsSpace().add(monkey);
    }
    
    public Geometry createMine(String name, Vector3f loc) { 
    //Dome mine = new Dome(Vector3f.ZERO, 2, 32, 1f,false);
    Box mine = new Box(1f, 1f, 1f);
    Geometry geo = new Geometry("Mine", mine);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //mat.setColor("Color",ColorRGBA.White);
    geo.setLocalTranslation(loc);
    geo.setMaterial(mat);
    geo.rotate(0, 0, 0.5f);
//    mine_phy = new RigidBodyControl(1f);
//    mine_phy.setGravity(90f);
    
    //geo.addControl(mine_phy);
    //|bulletAppState.getPhysicsSpace().add(mine_phy);
    rootNode.attachChild(geo);
    return geo;
  }
    
    public Geometry createExplosiveMine(String name, Vector3f loc) { 
    //Dome mine = new Dome(Vector3f.ZERO, 2, 32, 1f,false);
    Box mine = new Box(1f, 1f, 1f);
    Geometry geo = new Geometry("Mine", mine);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setTexture("NormalMap", assetManager.loadTexture("Textures/ColoredTex/Monkey.png"));
    //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //mat.setColor("Color",ColorRGBA.White);
    geo.setLocalTranslation(loc);
    geo.setMaterial(mat);
    geo.rotate(0, 0, 0.5f);
    //mine_phy = new RigidBodyControl(1f);
    
    //geo.addControl(mine_phy);
   // bulletAppState.getPhysicsSpace().add(mine_phy);
    rootNode.attachChild(geo);
    return geo;
  }
    
    private void makeExplosiveMines() {
        explosives = new Node("Collectables");
        for (int i = 0; i < 20; i++) {
            // randomize 3D coordinates
            Vector3f loc = new Vector3f(
                    FastMath.nextRandomInt(-75, 215),
                    2,
                    FastMath.nextRandomInt(-100, 80));
            
            explosives.attachChild(createExplosiveMine("mine",loc));
        }
        //rootNode.attachChild(explosives);
    }
    
    private void makemines() {
        collectables = new Node("Collectables");
        for (int i = 0; i < 20; i++) {
            // randomize 3D coordinates
            Vector3f loc = new Vector3f(
                    FastMath.nextRandomInt(-75, 215),
                    2,
                    FastMath.nextRandomInt(-100, 80));
            
            collectables.attachChild(createMine("mine",loc));
        }
        rootNode.attachChild(collectables);
    }
    
    public void makeInventory() {
    //DirectionalLight sun2 = new DirectionalLight();
    //sun2.setDirection(new Vector3f(-0.1f, 6.7f, -1.0f));
    float width = settings.getWidth();
    float height = settings.getHeight();
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color",ColorRGBA.White);
    Geometry cubeHUD = makeCube("Vertical last Line", 0f,0f,0f);
    cubeHUD.setMaterial(mat);
    cubeHUD.setLocalTranslation(width-2,0,0);
    cubeHUD.setLocalScale(3,height,0);
    guiNode.attachChild(cubeHUD);
    //guiNode.addLight(sun2);
    rootNode.attachChild(guiNode);
    
    }
    
    public void makeInventory2() {
    //DirectionalLight sun2 = new DirectionalLight();
    //sun2.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    float width = settings.getWidth();
    float height = settings.getHeight();
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color",ColorRGBA.DarkGray);
    Geometry cubeHUD = makeCube("Vertical middle Line", 0f,0f,0f);
    cubeHUD.setMaterial(mat);
    cubeHUD.setLocalTranslation(width/1.040f,0,0);
    cubeHUD.setLocalScale(2,height,1);
    guiNode.attachChild(cubeHUD);
   // guiNode.addLight(sun2);
    rootNode.attachChild(guiNode);
    
    }
    
    public void makeInventory3() {
    //DirectionalLight sun2 = new DirectionalLight();
    //sun2.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    float width = settings.getWidth();
    float height = settings.getHeight();
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color",ColorRGBA.White);
    Geometry cubeHUD = makeCube("Vertical first Line", 0f,0f,0f);
    cubeHUD.setMaterial(mat);
    cubeHUD.setLocalTranslation(width/1.080f,0,0);
    cubeHUD.setLocalScale(3,height,1);
    guiNode.attachChild(cubeHUD);
   // guiNode.addLight(sun2);
    rootNode.attachChild(guiNode);
    
    }
    
    
    
    protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
    Geometry cube = new Geometry(name, box);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color",ColorRGBA.LightGray);
    cube.setMaterial(mat1);
    return cube;
  }
    
    protected void makewall() {
        Box wall1=new Box(50,10,10);
        Geometry vault1=new Geometry("Vault1", wall1);
        
        
        
        Material mat3=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        RigidBodyControl vault1_phy=new RigidBodyControl(5);
        vault1.addControl(vault1_phy);
        vault1.getControl(RigidBodyControl.class).setKinematic(true);
        
        bulletAppState.getPhysicsSpace().add(vault1_phy);
        mat3.setColor("Color", ColorRGBA.Gray);
        vault1.setMaterial(mat3);
        //vault1.move(6,0,0);
        Texture cubeTex1= assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat3.setTexture("ColorMap", cubeTex1);
        vault1.setLocalTranslation(0,0,-120);
        
        rootNode.attachChild(vault1);
        
       
        
        Box wall2=new Box(80,10,10);
        Geometry vault2=new Geometry("Vault2", wall2);

        Material mat4=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        RigidBodyControl vault2_phy=new RigidBodyControl(5);
        vault2.addControl(vault2_phy);
        vault2.getControl(RigidBodyControl.class).setKinematic(true);
        
        bulletAppState.getPhysicsSpace().add(vault2_phy);
        mat4.setColor("Color", ColorRGBA.Gray);
        vault2.setMaterial(mat4);
        //vault2.move(6,0,0);
        Texture cubeTex2= assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat4.setTexture("ColorMap", cubeTex2);
        vault2.setLocalTranslation(150,0,-120);
        
        rootNode.attachChild(vault2);
    }
    
    public void createDoor(){
        vaultNode=new Node();
        Geometry vault;
        Box door=new Box(9,5,3);
        vault = new Geometry("Vault", door);
        vaultNode.attachChild(vault);
        
        
        Material mat=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        RigidBodyControl vault_phy=new RigidBodyControl(5);
        vault.addControl(vault_phy);
        vault.getControl(RigidBodyControl.class).setKinematic(true);
        
        bulletAppState.getPhysicsSpace().add(vault_phy);
        mat.setColor("Color", ColorRGBA.Brown);
        vault.setMaterial(mat);
        Texture cubeTex= assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat.setTexture("ColorMap", cubeTex);
        vault.move(6,0,0);
        vaultNode.setLocalTranslation(54,3,-112);
        rootNode.attachChild(vaultNode);
    }
    
    private void setupChaseCamera() {
        flyCam.setEnabled(false);
        //cam - the application camera
        //target - the spatial to follow
        //inputManager - the inputManager of the application to register inputs
        chaseCam = new ChaseCamera(cam, model, inputManager);
        //chaseCam.setChasingSensitivity(10f);
        //chaseCam.setTrailingSensitivity(0.05f);
    }
    
    private void setupKeys() {
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharUp", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharDown", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("CharSpace", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Char1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Char2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("CharThree", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("fireOn", new KeyTrigger(KeyInput.KEY_F));
        //inputManager.addMapping("fireOff", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("rotateDoor", new KeyTrigger(KeyInput.KEY_R));
        //inputManager.addMapping("Key", new KeyTrigger(KeyInput.KEY_K));
        //inputManager.addMapping("CharB", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("bananas", new KeyTrigger(KeyInput.KEY_B));
        //inputManager.addMapping("Char1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("CharEat", new KeyTrigger(KeyInput.KEY_E));
        
        inputManager.addListener(this, "CharLeft");
        inputManager.addListener(this, "CharRight");
        inputManager.addListener(this, "CharUp");
        inputManager.addListener(this, "CharDown");
        inputManager.addListener(this, "CharSpace");
        inputManager.addListener(this, "Char1");
        inputManager.addListener(this, "fireOn");
        inputManager.addListener(this, "fireOff");
        inputManager.addListener(this, "rotateDoor");
        inputManager.addListener(this, "Key");
        inputManager.addListener(this, "Shoot");
        inputManager.addListener(this, "CharB");
        inputManager.addListener(this, "bananas");
        inputManager.addListener(this, "CharThree");
        inputManager.addListener(this, "Char2");
        inputManager.addListener(this, "CharEat");
    }
    
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("CharLeft")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("CharRight")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        } else if (binding.equals("CharUp")) {
            if (value) {
                up = true;
            } else {
                up = false;
            }
        } else if (binding.equals("CharDown")) {
            if (value) {
                down = true;
            } else {
                down = false;
            }
        } else if (binding.equals("CharSpace")) {
            character.jump();
        }
        
       else if (binding.equals("Char1") && !value==true) {
             if (character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)*FastMath.RAD_TO_DEG)<60){
                 if(torch==false){
                     if(credit>=20){
                         
                         torch=true;
                        credit-=20;
                        hudTextVendor.setText("Torch purchased!\nPress F to turn it on or off");

                     }
                     else
                       hudTextVendor.setText("You need 40 credits to buy a torch");
                       // System.out.println("You need 40 credits to buy a torch");
                 }
                 else
                    // System.out.println("You have already purchased a torch");
                      hudTextVendor.setText("You have already purchased a torch");
                 
             }else if (character.getPhysicsLocation().distance(cassio.getPhysicsLocation())<10 && (Math.acos(vis3)*FastMath.RAD_TO_DEG)<60){
   
                     if(credit>=10){
                         
                        credit-=10;
                        bananas+=10;
                        
                        hudTextCassio.setText("10 Bananas purchased!");
                       
  
                     }else
                        hudTextCassio.setText(" Sorry!\nYou dont have\nenough credits to buy bananas");

            }
       }
            
            else if (binding.equals("fireOn") && !value==true) {
            if (torch==true){
                if (fireon==false){
                    createFire();   
                    createTorch();
                    rootNode.addLight(pl);
                    fireon=true;
                }
            
            
                else{
                    model.detachChild(fire);
                    model.detachChild(teapot);
                    rootNode.removeLight(pl);
                    fireon=false;
                }
            
        }else hudTextinfo.setText("You do not have\n a torch yet.");
            }
        else if (binding.equals("rotateDoor") && !value==true) {
            if (character.getPhysicsLocation().distance(vaultNode.getLocalTranslation())<20){
                //if (key==true)
                    vaultNode.rotate(0,1.6f,0);
                //else
                    hudTextinfo.setText("You need a key\n to open the door!");
            
            }
           
 
        }
        
        else if (binding.equals("Char2") && !value==true) {
             if (character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)*FastMath.RAD_TO_DEG)<60){
                 if (key==false){
                     if(mines>=3){
                        createKey();
                    //System.out.println("Key purchased");
                         hudTextVendor.setText("Key obtained");
                    key=true;

                     }
                     else
                       hudTextVendor.setText("Not enough mines collected.\n  3 required");
                 }
                 else
                     hudTextVendor.setText("You already have the key");
                 }
             else if (character.getPhysicsLocation().distance(cassio.getPhysicsLocation())<10 && (Math.acos(vis3)*FastMath.RAD_TO_DEG)<60){
                 
                     if(credit>=20){
                         
                    cannonballs+=100;
                    credit-=20;
                    
                    hudTextCassio.setText("100 cannonballs purchased!");
                   

                     }
                     else
                        hudTextCassio.setText("You need 20 credits to\n  buy cannonballs");
             }
 
        }
        else if (binding.equals("Shoot") && !value==true){
                
            if(cannonballs!=0){
              //  shoot=true;
                if (results2.size() > 0) {      
          closest2 = results2.getClosestCollision();
          float dist1 = closest2.getDistance();
          

          if(dist1<100 && dist1>10){
                
                //System.out.println("Mine destroyed");
                createExplosion();
                explosives.detachChild(closest2.getGeometry());
                hudTextinfo.setText("Mine has been disarmed");
                createExplMineImg();
//            Box guiBoxRed = new Box(new Vector3f(0f,0f,0f),1,1,1);
//            Geometry geoGuiBoxRed=new Geometry("Inventory Cube",guiBoxRed);
//            Material matRed = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            matRed.setColor("Color", ColorRGBA.Red);
//            geoGuiBoxRed.setMaterial(matRed);
//
//             guiNode.attachChild(geoGuiBoxRed);
//             geoGuiBoxRed.setLocalScale(10);
//             geoGuiBoxRed.setLocalTranslation(settings.getWidth()-60,displacement2,0);
//             displacement2+=30;
//             if (displacement>=settings.getHeight()){
//                 displacement=30;
//                 //column=40;
//             }
             
             credit+=10;
             explosiveMines++;
            
             //stamina-=1000;
             //System.out.println("Credits: "+credits);
          }else hudTextinfo.setText("");

                }
              
                cannonballs--;   
               
          
       
             
            
            /*     Sphere bullet=new Sphere(32,32,0.2f,true,false);
             Geometry bulletg = new Geometry("bullet", bullet);
             Material matBullet = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                bulletg.setMaterial(matBullet);
               // bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                bulletg.setLocalTranslation(character.getPhysicsLocation().add(.5f,0,0));
               // RigidBodyControl bulletNode = new BombControl(assetManager, bulletCollisionShape, 1);
                SphereCollisionShape bulletCollisionShape = new SphereCollisionShape(0.75f);
                RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 1);
                bulletNode.setLinearVelocity(character.getViewDirection().mult(25));
                bulletNode.setPhysicsLocation(character.getPhysicsLocation().add(5,5,5));
                
                bulletg.addControl(bulletNode);
                rootNode.attachChild(bulletg);
                getPhysicsSpace().add(bulletNode);
                cannonballs--;*/
       
              
            } else {
                 hudTextCannons.setText("You don't have enough cannonballs");
            }
         }
        
        else if (binding.equals("CharDodge") && !value==true){
            animationChannel.setAnim("Dodge",0.05f);
        }
        
        else if (binding.equals("bananas") && !value==true){
            if(bananas!=0){
                 if(monkeyOnMe==true){
                     
                     
                     //System.out.println("You have given monkey a banana");
                     hudText.setText("You have given monkey a banana");
                     bananas--;
                     //System.out.println("You now have "+bananas+ "bananas");
                     hudText.setText("Now you have "+bananas+ "bananas");
                   //  pursuit=false;
                     MonEating=true;
                 }  
                     
                     
                  //   animationChannel3.setAnim("Walk");
                  //   motionControl1.play();
                     
                    
 
            }
           
        }
         else if (binding.equals("CharThree") && !value==true) {
             if (character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)*FastMath.RAD_TO_DEG)<60){
                 
                     if(credit>=20){
                         
                        credit-=20;
                        food++;
                        hudTextVendor.setText("Food purchased!\nPress E to consume");
                     

                     }
                     else
                        hudTextVendor.setText("You need 20 credits to buy food");
                 }
                
             }
         else if(binding.equals("CharEat") && !value==true){
               if(food!=0){
                food--;
                if(stamina+1000>5000){
                           //  hudText1.setText("Food purchased!\nStamina increased by "+(10000-stamina));
                            stamina=5000;
                        }
                            
                        else{
                            //hudText1.setText("Food purchased!\nStamina increased by 1000");
                            stamina+=1000;
                        }
               }
         }
        }
    
//    public void onAction(String binding, boolean value, float tpf){
//        if (binding.equals("CharLeft")) {
//            if (value) {
//                left = true;
//            } else {
//                left = false;
//            }
//        } else if (binding.equals("CharRight")) {
//            if (value) {
//                right = true;
//            } else {
//                right = false;
//            }
//        }else if (binding.equals("CharUp")) {
//            if (value) {
//                up = true;
//            } else {
//                up = false;
//            }
//        } else if (binding.equals("CharDown")) {
//            if (value) {
//                down = true;
//            } else {
//                down = false;
//            }
//        } else if (binding.equals("CharSpace")) {
//            character.jump();
//        }
//        
//       else if (binding.equals("Char1") && !value==true) {
//             if (character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)*FastMath.RAD_TO_DEG)<60){
//                 if(torch==false){
//                     if(credit>=20){
//                         
//                         torch=true;
//                        credit-=20;
//                        hudText2.setText("Torch purchased!\nPress F to turn fire on/off");
//
//                     }
//                     else
//                       hudText2.setText("You need 40 credits to buy a torch");
//                       // System.out.println("You need 40 credits to buy a torch");
//                 }
//                 else
//                    // System.out.println("You have already purchased a torch");
//                      hudText2.setText("You have already purchased a torch");
//                 
//             }
//             
//             else if (character.getPhysicsLocation().distance(cassio.getPhysicsLocation())<10 && (Math.acos(vis3)*FastMath.RAD_TO_DEG)<60){
//   
//                     if(credit>=10){
//                         
//                        credit-=10;
//                        bananas+=10;
//                        
//                        hudText3.setText("10 Bananas purchased!");
//                       
//  
//                     }
//                     else
//                        hudText3.setText("You need 10 credits\n  to buy bananas");
//
//            }
//       }
//        
////        if (binding.equals("Char1") && torch == false && !value == true) {
////            if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation()) < 10 && (Math.acos(vis2)* FastMath.RAD_TO_DEG) < 60) {
////                if(credit >= 30) {
////                    torch = true;
////                    hudText2.setText("Torch purchased");
////                    credit -= 10;
////                    //System.out.println("Torch purchased");
////                }else if(torch == true) {
////                    hudText2.setText("Torch alredy purchased!");
////                    //System.out.println("Torch alredy purchased!");
////                }else  {
////                    hudText2.setText("Not enough credits!!");
////                    //System.out.println("Not enough credits!!");
////                }
////            }  
////        }
//        
//       else if (binding.equals("fireOn") && !value==true) {
//            if (torch==true){
//                if (fireon==false){
//                    createFire();   
//                    createTorch();
//                    rootNode.addLight(pl);
//                    fireon=true;
//                }else{
//                    model.detachChild(fire);
//                    model.detachChild(teapot);
//                    rootNode.removeLight(pl);
//                    fireon=false;
//                }
//            
//        }
//            }
//        
//       else if (binding.equals("rotateDoor") && !value == true) {
//            if (key == true) {
//            vaultNode.rotate(0, 1.6f, 0);
//                System.out.println("Door Opened!!!");
//            }else {
//                System.out.println("Key is required to open the door.");
//            }
//        }
//        
//       else if (binding.equals("Key") && !value == true) {
//          if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) < 10 && (Math.acos(vis3)* FastMath.RAD_TO_DEG) < 60) {
//            if(mines >= 3 && key == false) {
//                key = true;
//                hudText3.setText("Key purchased");
//                System.out.println("Key purchased");      
//            }else if(key == true) {
//                hudText3.setText("Key alrady purchased!");
//                System.out.println("Key alrady purchased!");
//            }else {
//                hudText3.setText("Not enough mines collected for key");
//                System.out.println("not enough mines collected for key.");
//            }
//            
//        }
//      }
//       
//       else if (binding.equals("Shoot") && !value == true) {
//           if(cannonballs > 0) {
//              //  shoot=true;
//                if (results2.size() > 0) {      
//          closest2 = results2.getClosestCollision();
//          float dist1 = closest2.getDistance();
//          
//
//          if(dist1<150 && dist1>10){
//                System.out.println("Mine destroyed");
//                createExplosion();
//                explosives.detachChild(closest2.getGeometry());
//                
//            Box explosiveMineCollected = new Box(new Vector3f(0f,0f,0f),1,1,1);
//            Geometry geoGuiBoxRed=new Geometry("Inventory Cube",explosiveMineCollected);
//            Material matRed = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            matRed.setColor("Color", ColorRGBA.Red);
//            geoGuiBoxRed.setMaterial(matRed);
//
//             guiNode.attachChild(geoGuiBoxRed);
//             geoGuiBoxRed.setLocalScale(10);
//             geoGuiBoxRed.setLocalTranslation(settings.getWidth()-40,displacement,0);
//             displacement+=30;
//             if (displacement>=settings.getHeight()){
//                 displacement=10;
//                 //column=40;
//             }
//             
//             credit+=10;
//             explosiveMines++;
//             //stamina-=1000;
//             System.out.println("Credits: " + credit);
//          }
//
//          }
//                
//            }
//        }
//        
//       else if (binding.equals("Char1") && !value == true) {
//                 if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) < 10 && (Math.acos(vis3)* FastMath.RAD_TO_DEG) < 60) {
//                     if(credit >= 10){
//                     hudText3.setText("you bought 10 bananas");
//                     //System.out.println("you bought 10 bananas");
//                     bananas += 10;
//                     credit -= 10;
//                         System.out.println(credit);
//                     }else {
//                         hudText3.setText("Sorry! you don't have enough credits.");
//                         //System.out.println("Sorry! you don't have enough credits.");
//                     }
//                 }
//              }
//       else if(binding.equals("CharB") && !value == true) {
//                if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) < 10 && (Math.acos(vis3)* FastMath.RAD_TO_DEG) < 60) {
//                    if(credit >= 10){
//                        System.out.println("you bought 10 cannoballs");
//                        credit -= 10;
//                        cannonballs += 10;
//                        System.out.println(credit);
//                    }else {
//                        System.out.println("Sorry! you don't have enough credits.");
//                    }
//                }
//                
//            }
//            
//           else if(binding.equals("bananas") && !value == true) {
//                if(bananas > 0 && monkeyOnMe == true) {
//                    bananas--;
//                    MonEating = true;
//                }else {
//                    System.out.println("Monkey needs bananas and you dont have any.");
//                }
////                if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) < 10 && (Math.acos(vis3)* FastMath.RAD_TO_DEG) < 60) {
////                    if(credit >= 10){
////                        System.out.println("you bought 10 cannoballs");
////                        credit -= 10;
////                        cannonballs += 10;
////                        System.out.println(credit);
////                    }else {
////                        System.out.println("Sorry! you don't have enough credits.");
////                    }
////                }
//                
//            }
//         }
    
    private void setupAnimationController() {
        animationControl = model.getControl(AnimControl.class);
        animationControl.addListener(this);
        animationChannel = animationControl.createChannel();
        
        animationControl2 = model2.getControl(AnimControl.class);
        animationControl2.addListener(this);
        animationChannel2 = animationControl2.createChannel();
        
        animationControl3 = model3.getControl(AnimControl.class);
        animationControl3.addListener(this);
        animationChannel3 = animationControl3.createChannel();
        
        animationControl4 = model4.getControl(AnimControl.class);
        animationControl4.addListener(this);
        animationChannel4 = animationControl4.createChannel();
        //animationChannel4.setAnim("Idle");
    }
    
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animName.equals("SliceVertical")) {
            channel.setAnim("HandsRelaxed", 0.50f);
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1f);
        }
    }
    
//    private void setupMotionPath() {
//        path = new MotionPath();
//        path.addWayPoint(new Vector3f(FastMath.nextRandomInt(-70,200), 1f,FastMath.nextRandomInt(-100,75)));
//        path.addWayPoint(new Vector3f(FastMath.nextRandomInt(-70,200), 1f,FastMath.nextRandomInt(-100,75)));
//        path.addWayPoint(new Vector3f(FastMath.nextRandomInt(-70,200), 1f,FastMath.nextRandomInt(-100,75)));
//        path.addWayPoint(new Vector3f(FastMath.nextRandomInt(-70,200), 1f,FastMath.nextRandomInt(-100,75)));
//        path.addWayPoint(new Vector3f(FastMath.nextRandomInt(-70,200), 1f,FastMath.nextRandomInt(-100,75)));
//        path.addWayPoint(new Vector3f(FastMath.nextRandomInt(-70,200), 1f,FastMath.nextRandomInt(-100,75)));
//        path.enableDebugShape(assetManager, rootNode);
//
//        motionControl = new MotionEvent(model4, path);
//        motionControl.setDirectionType(MotionEvent.Direction.Path);
//        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
//
//        motionControl.setInitialDuration(30f);
//        motionControl.setSpeed(0.6f);
//
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        final BitmapText wayPointsText = new BitmapText(guiFont, false);
//        wayPointsText.setSize(guiFont.getCharSet().getRenderedSize());
//
//        guiNode.attachChild(wayPointsText);
//
//        path.addListener(new MotionPathListener() {
//            final BitmapText wayPointsText = new BitmapText(guiFont, false);
//
//            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
//                animationChannel4.setAnim("Walk");
//                if (path.getNbWayPoints() == wayPointIndex + 1) {
//                    wayPointsText.setText(control.getSpatial().getName() + " Finish!!! ");
//                    animationChannel4.setAnim("Idle");
//                } else {
//                    wayPointsText.setText(control.getSpatial().getName() + " Reached way-point " + wayPointIndex);
//                    System.out.println("Way point  " + wayPointIndex + "reached,  object moving " + control.getSpatial().getName());
//                }
//                wayPointsText.setLocalTranslation((cam.getWidth() - wayPointsText.getLineWidth()) / 2, cam.getHeight(), 0);
//            }
//        });
//    }
 
    
    }

