package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.AnimationEvent;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.cinematic.events.SoundEvent;
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
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
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
public class Main extends SimpleApplication implements ActionListener,AnimEventListener {
    
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
    private Spatial sceneModel, sceneModel2, plasma, torchModel, barrel;
    public Spatial ex;
    private RigidBodyControl landscape, landscape2;
    private CharacterControl character, vendor, cassio, monkey;
    private Node model, model2, model3, model4, collectables, vaultNode, explosives;
    private ChaseCamera chaseCam;
    private boolean left,right,up,down, level;
    private boolean key, torch, pursuit, monkeyOnMe, MonEating, fireon, gameOver, gameWin = false;
    private boolean awayFromCassio, away, awayFromMonkey = true;
    Vector3f walkDirection = new Vector3f();
    private float airTime, vis2, vis3, vis4, timer, timer2, eatingTimer, x;
    private AnimControl animationControl,animationControl2, animationControl3, animationControl4;
    private AnimChannel animationChannel, animationChannel2, animationChannel3, animationChannel4;
    private Vector3f otoLocation, sinbadLocation, Oto2SinBad, cassioLocation, monkeyLocation, Oto2cassio, Oto2monkey, walkMonkey;
    DirectionalLight dl;
    AmbientLight al;
    PointLight pl;
    Ray ray2;
    BitmapText hudText, hudTextVendor, hudTextCassio, hudTextMonkey, hudTextinfo, hudTextGameOver, hudTextSTWarning ,hudTextWin,hudTextCannons;
    ParticleEmitter fire,explosion;
    CollisionResult closest2, closest;
    CollisionResults results, results2;
    private MotionPath path;
    private MotionEvent motionControl;
    float PROXIMITY = 4.0f;
    private Node sceneNode, n;
    RigidBodyControl mine_phy;
    private Cinematic cinematic;
    private CinematicEvent cameraMotionEvent;
    private AudioNode gun, nature, hello, collect;

    
    @Override
    public void simpleInitApp() {
       bulletAppState = new BulletAppState();
       stateManager.attach(bulletAppState);
       
       //hide the frames that are displayed in the bottom left side of the screen
       setDisplayFps(false);
       setDisplayStatView(false);
       
       //values of the item displayed in the GUI ex mines, food, stamina etc.
       hudText = new BitmapText(guiFont, false);        
       hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
       hudText.setColor(ColorRGBA.White);// font color
       hudText.setLocalTranslation(settings.getWidth()/1000f, settings.getHeight()/1.9f, 0); // position
       guiNode.attachChild(hudText);
            
       //text that are displayed when interact with Iago
       hudTextVendor = new BitmapText(guiFont, false);
       hudTextVendor.setSize(guiFont.getCharSet().getRenderedSize());
       hudTextVendor.setColor(ColorRGBA.White);
       hudTextVendor.setLocalTranslation(settings.getWidth()/2.7f, settings.getHeight()/1.15f, 0);
       guiNode.attachChild(hudTextVendor);
            
       //text that are displayed when interact with Cassio
       hudTextCassio = new BitmapText(guiFont, false);
       hudTextCassio.setSize(guiFont.getCharSet().getRenderedSize());
       hudTextCassio.setColor(ColorRGBA.White);
       hudTextCassio.setLocalTranslation(settings.getWidth()/2.7f, settings.getHeight()/1.15f, 0);
       guiNode.attachChild(hudTextCassio);
            
       //text that are displayed when interact with monkey
       hudTextMonkey = new BitmapText(guiFont, false);
       hudTextMonkey.setSize(30);
       hudTextMonkey.setColor(ColorRGBA.Red);
       hudTextMonkey.setLocalTranslation(settings.getWidth()/2.4f, settings.getHeight()/1.08f, 0);
       guiNode.attachChild(hudTextMonkey);
            
       //text that are displays some hints ex when you dont hav cannonballs, bananas, the key, the torch, etc.
       hudTextinfo = new BitmapText(guiFont, false);
       hudTextinfo.setSize(guiFont.getCharSet().getRenderedSize());
       hudTextinfo.setColor(ColorRGBA.Yellow);
       hudTextinfo.setLocalTranslation(settings.getWidth()/2.16f, settings.getHeight()/2.8f, 0);
       guiNode.attachChild(hudTextinfo);
            
       //text that are display the stamin warning when below 800
       hudTextSTWarning = new BitmapText(guiFont, false);
       hudTextSTWarning.setSize(45);
       hudTextSTWarning.setColor(ColorRGBA.Red);
       hudTextSTWarning.setLocalTranslation(settings.getWidth()/1.6f, settings.getHeight()/4f, 0);
       guiNode.attachChild(hudTextSTWarning);
            
       //text that are displayed when run out of cannonballs
       hudTextCannons = new BitmapText(guiFont, false);
       hudTextCannons.setSize(guiFont.getCharSet().getRenderedSize());
       hudTextCannons.setColor(ColorRGBA.Red);
       hudTextCannons.setLocalTranslation(settings.getWidth()/2.16f, settings.getHeight()/3.1f, 0);
       guiNode.attachChild(hudTextCannons);
            
       //text that are displayed when you die or run out of stamina
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
       
        //all functions that are called inside the SimpleInitApp
        food();
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
        createAmbientLight();
        makewall();
        createDoor();
        initAudio();
        setupAnimationController();
    }

    
    @Override
    public void simpleUpdate(float tpf) {
     
  //set up a timer to clean all the hudText areas on the GUI screen
  timer2 += tpf;
   if(timer2 > 2){
   hudTextinfo.setText("");
   hudTextCannons.setText("");
   timer2 = 0;
   }
        
      //this whole block executes when you start the game, until the win or the death of Othello!
      if(gameOver == false && gameWin == false) {
            //this condition checks the cords when Othello is on the map or if he falls down from the scene.
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
        //Point Light that is activated when useing the torch.    
        pl.setPosition(character.getPhysicsLocation());
        
        //The actual values in the GUI on the bottom left of the screen.
        hudText.setText("\n\nCredits: "+credit+"\n\nBananas: "+bananas+
                "\n\nCannonballs: "+cannonballs+"\n\nFood: "+food+ 
                "\n\nStamina: "+ stamina+ "\n\nMines: "+mines+
                "\n\nExplosives "+explosiveMines);
        
        //stamina begins to fall when the game starts.
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
        //Winning condition when Othello has collected 40 mines. 20 simple mines and 20 explosive mines.
        if(mines >= 20 && explosiveMines >= 20 && gameOver == false) {
            gameWin = true;
        }
        
         //Determine the speed of Othello, depending of the time of the day. ex (day, afternoon, night) changes in var x.
         Vector3f camDir = cam.getDirection().clone().multLocal(x); //speed
         Vector3f camLeft = cam.getLeft().clone().multLocal(x);
        
        //timer that always loops and changes from day,afternoon and night. Eventually the speed of Othello.
        timer += tpf;
        if (timer > 190) {
        timer=0;
        }
       // System.out.println(timer);
        if (timer > 50 && timer < 100 || timer > 150) {
         rootNode.removeLight(dl);
         x = 0.35f;
        // System.out.println(rootNode.);
         afternoonLight();
         //camDir = cam.getDirection().clone().multLocal(0.2f); //speed
         //camLeft = cam.getLeft().clone().multLocal(0.2f);
        } else if (timer > 100 && timer <= 150) {
         rootNode.removeLight(dl);
         x = 0.45f;
         nightLight();
         //camDir = cam.getDirection().clone().multLocal(0.1f); //speed
         //camLeft = cam.getLeft().clone().multLocal(0.1f);
        }else if (timer < 50) {
         rootNode.removeLight(dl);
         createLight();
         x = 0.25f;
          }
        
        
          //System.out.println(timer);
        
        //camera direction that is binded on othello and the respective animation.
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
        //otolocation is always the location of Othello
        otoLocation = character.getPhysicsLocation();
        //sinbadLocation is always the location of Iago
        sinbadLocation = vendor.getPhysicsLocation();
        //Oto2SinBad is always the location between Othello and Iago
        Oto2SinBad = sinbadLocation.subtract(otoLocation).multLocal(0.1f);
        
        //the view direction of Iago.
        Vector3f vD = new Vector3f();
        vD = vendor.getViewDirection().mult(-1f);
        //calculation of dot product between the Vectors to find the angle of visibility.
        vis2 = (Oto2SinBad.normalize()).dot((vD.normalize()));
        
        //if Othello is in the area of visibility
        if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)* FastMath.RAD_TO_DEG) < 60 && away == true) {
            //hello sound play, as greeting.
            hello.play();
            away = false;
            //perform an animation
            animationChannel2.setAnim("SliceVertical");
            hudTextVendor.setText("Hello Stranger!!!\nPress 1 to buy a torch so you can see in the night!\n"
                    + "Press 2 to get the key for the door\n"
                    + "or 3 to buy some food!");
            
            //System.out.println("Hello Stranger!!! Please press 1 to buy a torch!");
           }
        //if othello is outside of the area of visibility then set text to nothing and the boolean away to true.
        if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation()) >= 10 || (Math.acos(vis2)* FastMath.RAD_TO_DEG) > 60) {
            away = true;
           hudTextVendor.setText("");
        }
        //cassio location
        cassioLocation = cassio.getPhysicsLocation();
        //Oto2cassio is always the location between Othello and Cassio
        Oto2cassio = cassioLocation.subtract(otoLocation).multLocal(0.1f);
        
        Vector3f vD2 = new Vector3f();
        //view direction of Cassio
        vD2 = cassio.getViewDirection().mult(-1f);
        //calculation of dot product between the Vectors to find the angle of visibility.
        vis3 = (Oto2cassio.normalize()).dot((vD2.normalize()));
        
       //if Othello is in the area of visibility
        if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) < 10 && (Math.acos(vis3)* FastMath.RAD_TO_DEG) < 60 && awayFromCassio == true) {
            //hello sound play, as greeting
            hello.play();
            awayFromCassio = false;
            //perform an animation
            animationChannel3.setAnim("SliceVertical");
            hudTextCassio.setText("Hello im cassio!!!\nPlease press 1 to buy bananas!\n"
                    + "or 2 to buy cannonballs!");
            //System.out.println("Hello im cassio!!! Please press 1 to buy bananas!");
            
        }
        //if othello is outside of the area of visibility then set text to nothing and the boolean away to true.
        if(character.getPhysicsLocation().distance(cassio.getPhysicsLocation()) >= 10 || (Math.acos(vis3)* FastMath.RAD_TO_DEG) > 60) {
           awayFromCassio = true;
           hudTextCassio.setText("");
        }
        
        //location to monkey
        monkeyLocation = monkey.getPhysicsLocation();
        //Oto2monkey is always the location between Othello and monkey
        Oto2monkey = monkeyLocation.subtract(otoLocation).multLocal(0.1f);
        
        Vector3f vD3 = new Vector3f();
        //view direction of monkey
        vD3 = monkey.getViewDirection();
        //calculation of dot product between the Vectors to find the angle of visibility.
        vis4 = (Oto2monkey.normalize()).dot((vD3.normalize()));
        
        //if Othello is in the area of visibility
        if(character.getPhysicsLocation().distance(monkey.getPhysicsLocation()) < 80 && (Math.acos(vis4)* FastMath.RAD_TO_DEG) < 120 && awayFromMonkey == true && level == true) { 
            monkeyOnMe = true;
            pursuit = true;
            animationChannel4.setAnim("Walk");
            hudTextMonkey.setText("Haha i got you!!!");
            
            //if monkey is following Othello
            if (pursuit) {
            animationChannel4.setAnim("Walk");
            //if the distance between monkey and Othello is less than 4. PROXIMITY = 4
            if (monkeyLocation.distance(otoLocation) < PROXIMITY) {
                if(MonEating ==  false) {
                    stamina -= 20;
                    hudTextMonkey.setText("Hit!");
                    System.out.println("hit!");
                }
                walkMonkey = new Vector3f(0f, 0f, 0f);
                pursuit = false;
                //else monkey again follows Othello
            } else {
                // this sets the speed of the Monkey.
                walkMonkey = otoLocation.subtract(monkeyLocation).multLocal(0.009f);
                monkey.setViewDirection(walkMonkey.mult(-1f));
                animationChannel4.setAnim("Walk");
              }
            }
            
            monkey.setWalkDirection(walkMonkey);
      }else {
            hudTextMonkey.setText("");
        }
        
        //this code executes when the User is in the first level of the game.
        if(level == false) {
                
               //for each loop to see all the mines inside the collectable Node, and remove them when Othello is near.
               for(Spatial col:collectables.getChildren()) {
                   if(character.getPhysicsLocation().distance(col.getLocalTranslation()) < 5) {
                   animationChannel.setAnim("Dodge",2.1f);   
                   collectables.detachChild(col);
                   hudTextinfo.setText("Mine Collected");
                   mines++;
                   //sound when collect mines.
                   collect.playInstance();
                    //create the image for the mine in the GUI
                    createMineImg();
                
                    credit += 10;
                    }
               }
        }
        //this code exwcutes when the player in on the second level of the game.
        if(level == true) {            
          results2 = new CollisionResults();
          //using a ray to interact with the explosive mines in the second level.
          ray2 = new Ray(character.getPhysicsLocation().add(0f,-2f,0f), character.getViewDirection());
          explosives.collideWith(ray2, results2);
          
          
          //for each loop that sees all the explosives mines in the explosives Node and remove when Othello hits a mine causing him damage.
          for(Spatial ex:explosives.getChildren()){
            if(character.getPhysicsLocation().distance(ex.getLocalTranslation()) < 5) {
                hudTextinfo.setText("Ouch!!!");
                stamina -= 1000;
                System.out.println("Ouch!!!");
                createExplosion2(ex);
                explosives.detachChild(ex);
            }
          }
           //take the closest target that the ray aims to and display the message target on sight.
           if (results2.size() > 0) {
          closest2 = results2.getClosestCollision();
          float distance2 = closest2.getDistance();
          
          if(distance2 <= 100 && distance2 >= 15){
                hudTextinfo.setText("target on sight.");
                  }else hudTextinfo.setText("");
            if(distance2 < 5) {
                hudTextinfo.setText("Ouch!!!");
                stamina -= 1000;
                System.out.println("Ouch!!!");
                createExplosion();
                explosives.detachChild(closest2.getGeometry());
            }
           }else {
                timer2 += tpf;
                if(timer2 > 2){
                hudTextinfo.setText("");
                timer2 = 0;
                }
            }
                    //setting a different timer to calculate the eating time of the monkey
                    if(MonEating == true) {
                        eatingTimer += tpf;
                        System.out.println(eatingTimer);
                        if(eatingTimer > 20) {
                            MonEating = false;
                            eatingTimer = 0;
                        }
                    }
                    
        }
       
      }else {
          //this code executes if the game is over
          if (gameOver == true) {
          hudTextGameOver.setText("You died!\nGame Over");
          rootNode.detachAllChildren();
          }else {
            //this code executes if the player wins 
            gameWin = true;
            hudTextWin.setText("Congratulations!\nYou win!");
            rootNode.detachAllChildren();
          }
      }
          
        
}

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    //this method actually creates 2 boxes one inside the other
    //that increase and decrease depending witht he value of stamina.
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
    
    //method that creates the scene Town
    private void createTerrain() {
        sceneModel = assetManager.loadModel("Scenes/town/main.scene");
        sceneModel.setLocalScale(1f);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        getPhysicsSpace().add(sceneModel);
        
        rootNode.attachChild(sceneModel);
    }
    //method that creates the mines Spatial on the first level
    private Spatial createPlasma(Vector3f loc) {
        plasma = assetManager.loadModel("Models/mineModel/plasma.j3o");
        plasma.setLocalScale(0.5f);
        //rootNode.attachChild(barrel);
        plasma.setLocalTranslation(loc);
        
        return plasma;
    }
    //method that creates the key image
    private void createKey() {
        Picture pic = new Picture("Key Picture");
        pic.setImage(assetManager, "Textures/key/key_1_.png", true);
        pic.setWidth(settings.getWidth()/20);
        pic.setHeight(settings.getHeight()/20);
        pic.setPosition(settings.getWidth()/1.2f, settings.getHeight()/11f);
        guiNode.attachChild(pic);
    }
    //method that creates the torch image
    private void createtorch() {
        Picture torchimg = new Picture("Torch Picture");
        torchimg.setImage(assetManager, "Textures/imges/torch.png", true);
        torchimg.setWidth(settings.getWidth()/18);
        torchimg.setHeight(settings.getHeight()/11);
        torchimg.setPosition(settings.getWidth()/1.28f, settings.getHeight()/13.35f);
        guiNode.attachChild(torchimg);
    }
    //method that creates the banana picture
    private void food() {
        Picture banana = new Picture("banana Picture");
        banana.setImage(assetManager, "Textures/imges/banana.png", true);
        banana.setWidth(settings.getWidth()/25);
        banana.setHeight(settings.getHeight()/20);
        banana.setPosition(settings.getWidth()/14f, settings.getHeight()/2.65f);
        guiNode.attachChild(banana);
        
        //method that creates the food picture
        Picture food = new Picture("food Picture");
        food.setImage(assetManager, "Textures/imges/food.png", true);
        food.setWidth(settings.getWidth()/25);
        food.setHeight(settings.getHeight()/17);
        food.setPosition(settings.getWidth()/14f, settings.getHeight()/3.95f);
        guiNode.attachChild(food);
        
        //method that creates the cannonballs picture
        Picture cannonballs = new Picture("cannonballs Picture");
        cannonballs.setImage(assetManager, "Textures/imges/cannonballs.png", true);
        cannonballs.setWidth(settings.getWidth()/30);
        cannonballs.setHeight(settings.getHeight()/20);
        cannonballs.setPosition(settings.getWidth()/10f, settings.getHeight()/3.2f);
        guiNode.attachChild(cannonballs);
        
        //method that creates the coins picture
        Picture coins = new Picture("coins Picture");
        coins.setImage(assetManager, "Textures/imges/coin.png", true);
        coins.setWidth(settings.getWidth()/30);
        coins.setHeight(settings.getHeight()/20);
        coins.setPosition(settings.getWidth()/14f, settings.getHeight()/2.33f);
        guiNode.attachChild(coins);
    }
    
    //method that creates the mine picture and adding it in the GUI
    private void createMineImg() {
        Picture pic = new Picture("Mine Picture");
        pic.setImage(assetManager, "Textures/Mines/mine.png", true);
        pic.setWidth(settings.getWidth()/30);
        pic.setHeight(settings.getHeight()/23);
        pic.setPosition(settings.getWidth()/1.079f, settings.getHeight()/50f+displacement);
        displacement += 35;
        guiNode.attachChild(pic);
    }
    //method that creates the explosive mine picture and adding it in the GUI
    private void createExplMineImg() {
        Picture pic = new Picture("Explosive mine Picture");
        pic.setImage(assetManager, "Textures/Mines/ex_mine.png", true);
        pic.setWidth(settings.getWidth()/20);
        pic.setHeight(settings.getHeight()/25);
        pic.setPosition(settings.getWidth()/1.045f, settings.getHeight()/50f+displacement2);
        displacement2 += 35;
        guiNode.attachChild(pic);

    }
    //method that creates the Sky as a 6 pictured image inside an array
    private void createSky() {
        Texture[] skyTex = new Texture[6];
        skyTex[0] = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        skyTex[1] = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        skyTex[2] = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        skyTex[3] = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        skyTex[4] = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        skyTex[5] = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, skyTex[0], skyTex[1], skyTex[2], skyTex[3], skyTex[4], skyTex[5]);
        rootNode.attachChild(sky);
    }
    //method that creates the day light for the game
    private void createLight() {
        Vector3f direction = new Vector3f(-0.1f, -0.7f, -1).normalizeLocal();
        dl = new DirectionalLight();
        dl.setDirection(direction);
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);
    }
    //method that creates the Ambient Light that makes Othello a bit visible during the night.
    private void createAmbientLight() {
        al = new AmbientLight();
        al.setColor(ColorRGBA.Yellow);
    }
    //method that creates PointLight that light around Othello during night.
    private void createPointLight() {
        pl = new PointLight();
        pl.setRadius(30);
        pl.setColor(ColorRGBA.Yellow);
    }
    //method that creates the night light for the game. 
    private void nightLight() {
         Vector3f direction = new Vector3f(0.5348667f, -0.6787754f, -0.50317144f).normalizeLocal();
         dl = new DirectionalLight();
         dl.setDirection(direction);
         ColorRGBA nightColor = new ColorRGBA(.04f, .04f, .2f, 1);
         dl.setColor(nightColor);
         rootNode.addLight(dl);
        }
     //method that creates the afternoon light for the game. 
     private void afternoonLight() {
         Vector3f direction = new Vector3f(0.5348667f, -0.6787754f, -0.50317144f).normalizeLocal();
         dl = new DirectionalLight();
         dl.setDirection(direction);
         ColorRGBA nightColor = new ColorRGBA(.06f, .06f, .2f, 1);
         dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f).multLocal(0.6f));
         rootNode.addLight(dl);
     }
    //method that creates Othello.
    private void createCharacter() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1f, 1);
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
    //method that creates the torch.
    private void createTorch() {
        torchModel = assetManager.loadModel("Models/torch/torch1.j3o");
        torchModel.setLocalScale(0.5f);
        torchModel.rotate(3f, 0f, 0f);
        torchModel.setLocalTranslation(1.09f, -4f, 0.4f);
        
        SkeletonControl skeletonControl = model.getControl(SkeletonControl.class);
        n = skeletonControl.getAttachmentsNode("hand.right");
        n.attachChild(torchModel);
    }
    //method that creates Cassio NPC.
    private void createCassio() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1.6f, 1);
        cassio = new CharacterControl(capsule, 2.75f);
        model3 = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        model3.setLocalScale(0.4f);
        model3.addControl(cassio);
        getPhysicsSpace().add(cassio);
    }
    //method that creates the fire on top of the torch.
    public void createFire(){
    fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
    Material mat_red = new Material(assetManager, 
            "Common/MatDefs/Misc/Particle.j3md");
    mat_red.setTexture("Texture", assetManager.loadTexture(
            "Effects/Explosion/flame.png"));
    SkeletonControl skeletonControl = model.getControl(SkeletonControl.class);
        n = skeletonControl.getAttachmentsNode("hand.right");
        n.attachChild(fire);
    fire.setMaterial(mat_red);
    fire.setImagesX(2); 
    fire.setImagesY(2); // 2x2 texture animation
    fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
    fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
    fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
    fire.setStartSize(0.8f);
    fire.setEndSize(0.1f);
    fire.setGravity(0, 0, 0);
    fire.setLowLife(1f);
    fire.setHighLife(3f);
    fire.getParticleInfluencer().setVelocityVariation(0.3f);
    fire.setLocalTranslation(1.54f, -8.5f, -0.75f);
    }
    //method that creates the explosion when you shoot a mine in the second stage.
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
    //method that creates the explosion when Othello steps into a mine.
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
    //method that creates Iago.
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
    //method that creates Monkey.
    private void createMonkey() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.6f, 0.6f);
        monkey = new CharacterControl(capsule, 2.75f);
        model4 = (Node) assetManager.loadModel("Models/monkeyExport/Jaime.j3o");
        model4.setLocalScale(2f);
        model4.addControl(monkey);
        monkey.setPhysicsLocation(new Vector3f(FastMath.nextRandomInt(-70,200), 2.01976f, FastMath.nextRandomInt(-100,75)));
        //rootNode.attachChild(model4);
        getPhysicsSpace().add(monkey);
    }
    //method that creates mine
//    public Geometry createMine(String name, Vector3f loc) { 
//    //Dome mine = new Dome(Vector3f.ZERO, 2, 32, 1f,false);
//    Box mine = new Box(1f, 1f, 1f);
//    Geometry geo = new Geometry("Mine", mine);
//    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//    //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//    //mat.setColor("Color",ColorRGBA.White);
//    geo.setLocalTranslation(loc);
//    geo.setMaterial(mat);
//    geo.rotate(0, 0, 0.5f);
////    mine_phy = new RigidBodyControl(1f);
////    mine_phy.setGravity(90f);
//    
//    //geo.addControl(mine_phy);
//    //|bulletAppState.getPhysicsSpace().add(mine_phy);
//    rootNode.attachChild(geo);
//    return geo;
//  }
    //method that creates the explosive mines in a Dome shape.
    public Geometry createExplosiveMine(String name, Vector3f loc) { 
    Dome mine = new Dome(Vector3f.ZERO, 2, 32, 1f,false);
    Geometry geo = new Geometry("Mine", mine);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setColor("Diffuse",ColorRGBA.Red);
    mat.setBoolean("UseMaterialColors", true);
    geo.setLocalTranslation(loc);
    geo.setMaterial(mat);
    return geo;
  }
    //method that creates the explosive mines calling the method createExplosiveMine()
    //and deploy them randomly on the map.
    private void makeExplosiveMines() {
        explosives = new Node("Collectables");
        for (int i = 0; i < 27; i++) {
            // randomize 3D coordinates
            Vector3f loc = new Vector3f(
                    FastMath.nextRandomInt(-75, 215),
                    0,
                    FastMath.nextRandomInt(-100, 80));
            
            explosives.attachChild(createExplosiveMine("mine",loc));
        }
        //rootNode.attachChild(explosives);
    }
    //method that creates the mines calling the method createPlasma()
    //and deploy them randomly on the map.
    private void makemines() {
        collectables = new Node("Collectables");
        for (int i = 0; i < 27; i++) {
            // randomize 3D coordinates
            Vector3f loc = new Vector3f(
                    FastMath.nextRandomInt(-75, 215),
                    0,
                    FastMath.nextRandomInt(-100, 80));
            
            collectables.attachChild(createPlasma(loc));
        }
        rootNode.attachChild(collectables);
    }
    //method that creates the inventory line calling the makeCube method (last line)
    public void makeInventory() {
    float width = settings.getWidth();
    float height = settings.getHeight();
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color",ColorRGBA.White);
    Geometry cubeHUD = makeCube("Vertical last Line", 0f,0f,0f);
    cubeHUD.setMaterial(mat);
    cubeHUD.setLocalTranslation(width-2,0,0);
    cubeHUD.setLocalScale(3,height,0);
    guiNode.attachChild(cubeHUD);
    rootNode.attachChild(guiNode);
    
    }
    //method that creates the inventory line calling the makeCube method (middle line)
    public void makeInventory2() {
    float width = settings.getWidth();
    float height = settings.getHeight();
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color",ColorRGBA.DarkGray);
    Geometry cubeHUD = makeCube("Vertical middle Line", 0f,0f,0f);
    cubeHUD.setMaterial(mat);
    cubeHUD.setLocalTranslation(width/1.040f,0,0);
    cubeHUD.setLocalScale(2,height,1);
    guiNode.attachChild(cubeHUD);
    rootNode.attachChild(guiNode);
    
    }
    //method that creates the inventory line calling the makeCube method(first line)
    public void makeInventory3() {
    float width = settings.getWidth();
    float height = settings.getHeight();
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color",ColorRGBA.White);
    Geometry cubeHUD = makeCube("Vertical first Line", 0f,0f,0f);
    cubeHUD.setMaterial(mat);
    cubeHUD.setLocalTranslation(width/1.080f,0,0);
    cubeHUD.setLocalScale(3,height,1);
    guiNode.attachChild(cubeHUD);
    rootNode.attachChild(guiNode);
    
    }
    //this is the actual line that is a box with x ,y, z determined by the methods for the inventory.
    protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
    Geometry cube = new Geometry(name, box);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color",ColorRGBA.LightGray);
    cube.setMaterial(mat1);
    return cube;
  }
    //method that creates the wall with 2 boxes and the same thexture.
    protected void makewall() {
        //the first wall
        Box wall1 = new Box(50,10,10);
        Geometry vault1 = new Geometry("Vault1", wall1);
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        RigidBodyControl vault1_phy = new RigidBodyControl(5);
        vault1.addControl(vault1_phy);
        vault1.getControl(RigidBodyControl.class).setKinematic(true);
        bulletAppState.getPhysicsSpace().add(vault1_phy);
        mat3.setColor("Color", ColorRGBA.Gray);
        vault1.setMaterial(mat3);
        Texture cubeTex1= assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat3.setTexture("ColorMap", cubeTex1);
        vault1.setLocalTranslation(0,0,-120);
        rootNode.attachChild(vault1);
        
        //the seconf wall
        Box wall2 = new Box(80,10,10);
        Geometry vault2 = new Geometry("Vault2", wall2);
        Material mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        RigidBodyControl vault2_phy = new RigidBodyControl(5);
        vault2.addControl(vault2_phy);
        vault2.getControl(RigidBodyControl.class).setKinematic(true);
        bulletAppState.getPhysicsSpace().add(vault2_phy);
        mat4.setColor("Color", ColorRGBA.Gray);
        vault2.setMaterial(mat4);
        Texture cubeTex2= assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat4.setTexture("ColorMap", cubeTex2);
        vault2.setLocalTranslation(150,0,-120);
        rootNode.attachChild(vault2);
    }
    //method that creates the door between the 2 walls.
    public void createDoor(){
        vaultNode = new Node();
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
    //method that sets tha camera to follow Othello
    private void setupChaseCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, model, inputManager);
    }
    //method setupKeys sets all the mappings and listeners.
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
        inputManager.addMapping("rotateDoor", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Shoot", new MouseButtonTrigger(mouseInput.BUTTON_LEFT));
        inputManager.addMapping("bananas", new KeyTrigger(KeyInput.KEY_B));
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
    
    //method that runs when the User presses one of coresponding keys in the keyboard
    //or the left mouse button. 
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
        //options that are displayed from Iago if Othello is inside the area of visibility
       else if (binding.equals("Char1") && !value==true) {
             if (character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)*FastMath.RAD_TO_DEG)<60){
                 if(torch==false){
                     if(credit>=40){
                         createtorch();
                         torch=true;
                        credit-=40;
                        hudTextVendor.setText("Torch purchased!\nPress F to turn it on or off");

                     }
                     else
                       hudTextVendor.setText("You need 40 credits to buy a torch");
                 }
                 else
                      hudTextVendor.setText("You have already purchased a torch");
                 
             }
             //or to purchase bananas from Cassio.
             else if (character.getPhysicsLocation().distance(cassio.getPhysicsLocation())<10 && (Math.acos(vis3)*FastMath.RAD_TO_DEG)<60){
   
                     if(credit>=10){
                         
                        credit-=10;
                        bananas+=10;
                        
                        hudTextCassio.setText("10 Bananas purchased!");
                       
  
                     }else
                        hudTextCassio.setText(" Sorry!\nYou dont have\nenough credits to buy bananas\n you need 10 credits.");

            }
       }
            //listener to set the torch on or off.
            else if (binding.equals("fireOn") && !value==true) {
            if (torch==true){
                if (fireon==false){
                    createFire();   
                    createTorch();
                    rootNode.addLight(pl);
                    model.addLight(al);
                    fireon=true;
                }else{
                    n.detachChild(fire);
                    n.detachChild(torchModel);
                    rootNode.removeLight(pl);
                    model.removeLight(al);
                    fireon=false;
                }
            
        }else hudTextinfo.setText("You do not have\n a torch yet.");
            }
        else if (binding.equals("rotateDoor") && !value==true) {
            if (character.getPhysicsLocation().distance(vaultNode.getLocalTranslation())<20){
                if (key==true)
                    vaultNode.rotate(0,1.6f,0);
                else
                    hudTextinfo.setText("You need a key\n to open that door!");
            
            }
           
 
        }
        //listener to purchase the key from Iago
        else if (binding.equals("Char2") && !value==true) {
             if (character.getPhysicsLocation().distance(vendor.getPhysicsLocation())<10 && (Math.acos(vis2)*FastMath.RAD_TO_DEG)<60){
                 if (key==false){
                     if(mines>=20){
                        createKey();
                    //System.out.println("Key purchased");
                         hudTextVendor.setText("Key obtained");
                    key=true;

                     }
                     else
                       hudTextVendor.setText("Not enough mines collected.\nYou need 20 mines to obtain a key");
                 }
                 else
                     hudTextVendor.setText("You already have the key");
                 }
             //or to buy cannonballs from Cassio.
             else if (character.getPhysicsLocation().distance(cassio.getPhysicsLocation())<10 && (Math.acos(vis3)*FastMath.RAD_TO_DEG)<60){
                 
                     if(credit>=20){
                         
                    cannonballs+=50;
                    credit-=20;
                    
                    hudTextCassio.setText("50 cannonballs purchased!");
                   

                     }
                     else
                        hudTextCassio.setText("You need 20 credits to\n  buy cannonballs");
             }
 
        }
        //listener that triggers when the User shoots.
        else if (binding.equals("Shoot") && !value==true){
                 
            if(cannonballs!=0){
                gun.playInstance();
              //  shoot=true;
                if (results2.size() > 0) {      
          closest2 = results2.getClosestCollision();
          float dist1 = closest2.getDistance();
          
          //create a flare in the position of the mine and set the text to Mine has been diffuse.
          if(dist1<100 && dist1>10){
                createExplosion();
                explosives.detachChild(closest2.getGeometry());
                hudTextinfo.setText("Mine has been diffuse");
                createExplMineImg();
                credit+=10;
                explosiveMines++;
          }else 
              hudTextinfo.setText("");

                }
              cannonballs--;   
               
          /////////////////////////////////////////////////
          // attempt for the creation of the cannonballs.//
          // cannonbals are not matched in the Othello's //
          //                direction.                   //
          //  uncomment to run the code below.           //
          //                                             //
          /////////////////////////////////////////////////
       
             
                //code for the creation of the cannonballs.
//                Sphere bullet=new Sphere(32,32,0.2f,true,false);
//                Geometry bulletg = new Geometry("bullet", bullet);
//                 Material matBullet = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//                bulletg.setMaterial(matBullet);
//                bulletg.setLocalTranslation(character.getPhysicsLocation().add(.5f,0,0));
//                SphereCollisionShape bulletCollisionShape = new SphereCollisionShape(0.75f);
//                RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 1);
//                bulletNode.setLinearVelocity(character.getViewDirection().mult(25));
//                bulletNode.setPhysicsLocation(character.getViewDirection());
//                bulletg.addControl(bulletNode);
//                rootNode.attachChild(bulletg);w
//                getPhysicsSpace().add(bulletNode);
       
              
            } else {
                 hudTextCannons.setText("You don't have enough cannonballs");
            }
         }
        
        //listener to give a banana to the monkey
         else if (binding.equals("bananas") && !value==true){
            if(bananas!=0){
                 if(monkeyOnMe==true){
                     hudTextMonkey.setText("Yum Yum!");
                     bananas--;
                     hudTextinfo.setText("Now you have "+bananas+ "bananas");
                     MonEating=true;
                 }
            }
         }
         //listener to buy food.
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
         //listener to consume the food.
         else if(binding.equals("CharEat") && !value==true){
               if(food!=0){
                food--;
                if(stamina+1000>5000){
                            stamina=5000;
                        }
                            
                        else{
                            stamina+=1000;
                        }
               }
         }
        }
    //Animation Controllers of Othello and NPC's
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
    
    private void initAudio() {
    /* gun shot sound is to be triggered by a mouse click. */
    gun = new AudioNode(assetManager, "Sounds/Effects/gun2.wav", false);
    gun.setPositional(false);
    gun.setLooping(false);
    gun.setVolume(0.5f);
    rootNode.attachChild(gun);
 
    /* nature sound - keeps playing in a loop. */
    nature = new AudioNode(assetManager, "Sounds/Environment/forest.ogg", false);
    nature.setLooping(true);  // activate continuous playing
    nature.setPositional(true);   
    nature.setVolume(3);
    rootNode.attachChild(nature);
    nature.play(); // play continuously!
    
    hello = new AudioNode(assetManager, "Sounds/Environment/hello.ogg", false);
    hello.setLooping(false);
    hello.setPositional(true);   
    hello.setVolume(3);
    rootNode.attachChild(hello);
    
    collect = new AudioNode(assetManager, "Sounds/Effects/Bang.wav", false);
    collect.setLooping(false);
    collect.setPositional(true);   
    collect.setVolume(3);
    rootNode.attachChild(collect);

  }
}

