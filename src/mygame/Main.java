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
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

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
    private int displacement = 10;
    private int displacement1 =10;
    private int credit = 0;
    private int stamina = 10000;
    private Spatial sceneModel;
    private RigidBodyControl landscape;
    private CharacterControl character, vendor;
    private Node model, model2, collectables, vaultNode, staminaNode;
    private ChaseCamera chaseCam;
    private boolean left,right,up,down,torch, fireon;
    private boolean away = true;
    Vector3f walkDirection = new Vector3f();
    private float airTime, vis2, timer;
    private AnimControl animationControl,animationControl2 ;
    private AnimChannel animationChannel, animationChannel2;
    private Vector3f otoLocation, sinbadLocation, Oto2SinBad;
    DirectionalLight dl;
    PointLight pl;
    BitmapText hudText;
    ParticleEmitter fire;
    Spatial teapot;
    
    @Override
    public void simpleInitApp() {
       bulletAppState = new BulletAppState();
       stateManager.attach(bulletAppState);
      
       
        setupKeys();
        createTerrain();
        createSky();
        createLight();
        createCharacter();
        setupChaseCamera();
        createVendor();
        setupAnimationController();
        makemines();
        makeInventory();
        createPointLight();
        makewall();
        createDoor();
    }

    @Override
    public void simpleUpdate(float tpf) {
        pl.setPosition(character.getPhysicsLocation());
        
        stamina -= tpf;
        Stamina();
         
         Vector3f camDir = cam.getDirection().clone().multLocal(0.3f); //speed
         Vector3f camLeft = cam.getLeft().clone().multLocal(0.3f);
        
        timer += tpf;
        if (timer > 40) {
        timer=0;
        }
       // System.out.println(timer);
        if (timer > 10 && timer < 20 || timer > 30) {
         rootNode.removeLight(dl);
        // System.out.println(rootNode.);
         afternoonLight();
        } else if (timer > 20 && timer <= 30) {
         rootNode.removeLight(dl);
         nightLight();
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
        
         //System.out.println(character.getPhysicsLocation());

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
            System.out.println("Hello Stranger!!! Please press 1 to buy a torch!");
           
            
        }
        if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation()) >= 10 || (Math.acos(vis2)* FastMath.RAD_TO_DEG) > 60) {
           away = true;
        }
        
        CollisionResults results = new CollisionResults();
          Ray ray = new Ray(character.getPhysicsLocation(), character.getViewDirection());
          collectables.collideWith(ray, results);
          
                   if (results.size() > 0) {
          CollisionResult closest = results.getClosestCollision();
          float distance = closest.getDistance();
            if(distance < 15) {
                System.out.println("Mine collected");
                collectables.detachChild(closest.getGeometry());
                
            Box guiBox = new Box(new Vector3f(0f,0f,0f),1,1,1);
            Geometry geoGuiBox;
            geoGuiBox = new Geometry("Inventory Cube",guiBox);
             Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat1.setColor("Color",ColorRGBA.Blue);
            geoGuiBox.setMaterial(mat1);
            
             guiNode.attachChild(geoGuiBox);
             geoGuiBox.setLocalScale(10);
             geoGuiBox.setLocalTranslation(settings.getWidth()-23,displacement,0);
                displacement+=30;
                
                credit += 10;
                System.out.println("credits : " + credit);
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
    
    private void Stamina() {
        staminaNode = new Node();
        Box staminaBox = new Box(new Vector3f(0f,0f,0f),1,30,1);
        Geometry stamGuiBox = new Geometry("Stamina Cube",staminaBox);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Red);
        stamGuiBox.setMaterial(mat1);
        staminaNode.attachChild(stamGuiBox);
              
             stamGuiBox.setLocalScale(10);
             stamGuiBox.setLocalTranslation(20,displacement1,0);
             displacement1+=20;
             //guiNode.detachChild(staminaNode);
       
             displacement1=10;
             guiNode.attachChild(staminaNode);
            }
    
    private void createTerrain() {
        sceneModel = assetManager.loadModel("Scenes/town/main.scene");
        sceneModel.setLocalScale(1.0f);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        getPhysicsSpace().add(sceneModel);
        
        rootNode.attachChild(sceneModel);
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
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1.6f, 1);
        character = new CharacterControl(capsule, 2.75f);
        model = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        model.setLocalScale(0.4f);
        character.setJumpSpeed(40f);
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
    
    private void createVendor() {
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(2f, 1.6f, 1);
        vendor = new CharacterControl(capsule, 2.75f);
        model2 = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");
        model2.setLocalScale(0.4f);
        vendor.setJumpSpeed(40f);
        vendor.setGravity(90f);
        vendor.setFallSpeed(50f);
        model2.addControl(vendor);
        vendor.setPhysicsLocation(new Vector3f(-6.246029f, 3.001349f, 10.4667f));
        cam.setLocation(vendor.getPhysicsLocation());
        rootNode.attachChild(model2);
        getPhysicsSpace().add(vendor);
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
    //mine_phy = new RigidBodyControl(1f);
    
    //geo.addControl(mine_phy);
   // bulletAppState.getPhysicsSpace().add(mine_phy);
    rootNode.attachChild(geo);
    return geo;
  }
    
    private void makemines() {
        collectables = new Node("Collectables");
        for (int i = 0; i < 40; i++) {
            // randomize 3D coordinates
            Vector3f loc = new Vector3f(
                    FastMath.nextRandomInt(-75, 215),
                    2,
                    FastMath.nextRandomInt(-100, 80));
            
            collectables.attachChild(createMine("mine"+i,loc));
        }
        rootNode.attachChild(collectables);
    }
    
    public void makeInventory() {
    //DirectionalLight sun2 = new DirectionalLight();
    //sun2.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    float width = settings.getWidth();
    float height = settings.getHeight();
    Geometry cubeHUD = makeCube("Vertical Gui Line", 0f,0f,0f);
    cubeHUD.setLocalTranslation(width-50f,0,0);
    cubeHUD.setLocalScale(2,height,1);
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
        inputManager.addMapping("fireOn", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("fireOff", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("rotateDoor", new KeyTrigger(KeyInput.KEY_R));
        
        inputManager.addListener(this, "CharLeft");
        inputManager.addListener(this, "CharRight");
        inputManager.addListener(this, "CharUp");
        inputManager.addListener(this, "CharDown");
        inputManager.addListener(this, "CharSpace");
        inputManager.addListener(this, "Char1");
        inputManager.addListener(this, "fireOn");
        inputManager.addListener(this, "fireOff");
        inputManager.addListener(this, "rotateDoor");
    }
    
    public void onAction(String binding, boolean value, float tpf){
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
        }else if (binding.equals("CharUp")) {
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
        }else if (binding.equals("Char1") && torch == false) {
            if(character.getPhysicsLocation().distance(vendor.getPhysicsLocation()) < 10 && (Math.acos(vis2)* FastMath.RAD_TO_DEG) < 60) {
                if(credit >= 30){
                torch = true;
                System.out.println("Torch purchased");
                }else {
                    System.out.println("Not enough credits!!");
                }
            }  
        }else if (binding.equals("fireOn")) {
            if(torch == true && fireon == false){
                createFire();
                createTorch();
                rootNode.addLight(pl);
                fireon = true;
            }
        }else if (binding.equals("fireOff")) {
            if(fireon == true && torch == true){
            model.detachChild(fire);
            model.detachChild(teapot);
            rootNode.removeLight(pl);
            fireon = false;
            }
        }else if (binding.equals("rotateDoor")) {
            vaultNode.rotate(0, 0.8f, 0);
            
        }
    }
    
    private void setupAnimationController() {
        animationControl = model.getControl(AnimControl.class);
        animationControl.addListener(this);
        animationChannel = animationControl.createChannel();
        
        animationControl2 = model2.getControl(AnimControl.class);
        animationControl2.addListener(this);
        animationChannel2 = animationControl2.createChannel();
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
}
