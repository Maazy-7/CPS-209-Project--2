import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import java.awt.event.*;

public class window extends JPanel implements KeyListener, MouseListener, MouseMotionListener
{
    ArrayList<mesh> meshes = new ArrayList<>();
    mesh monke = new mesh(new Color(200,0,50));
    mesh cube = new mesh(new Color(200,0,50));
    mesh sphere = new mesh(new Color(200,0,50));
    mesh elephant = new mesh(new Color(200,0,50));
    mesh eggplant = new mesh(new Color(200,0,50));
    mesh spring = new mesh(new Color(200,0,50));
    mesh ring = new mesh(new Color(200,0,50));
    boolean debug = false;

    public window() 
    {
        JFrame frame = new JFrame("Window");
        frame.setSize(util.width, util.height);
        this.setBackground(new Color(135, 137, 140));
        this.setPreferredSize(new Dimension(util.width,util.height));
        frame.add(this);
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        BufferedImage cursor = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0,0), "blank cursor");
        frame.setCursor(blankCursor);
        frame.setUndecorated(true);
        if (!debug) {frame.setExtendedState(JFrame.MAXIMIZED_BOTH);}
        else {frame.pack();}
        frame.setVisible(true);

        //calling start method which creates and initalizes the meshes
        start();
    }

    int[] xpos = {-18,-12,-6,0,6,12,18};
    String[] objectModels = 
    {"src/monke.obj","src/cube.obj","src/icoSphere.obj","src/elephant.obj","src/eggplant.obj","src/spring.obj","src/ring.obj"};
    void start() 
    {
        meshes.add(monke);
        meshes.add(cube);
        meshes.add(sphere);
        meshes.add(elephant);
        meshes.add(eggplant);
        meshes.add(spring);
        meshes.add(ring);

        for (int i = 0; i < meshes.size(); i++) 
        {
            try
            {
                meshes.get(i).tris = meshes.get(i).loadModel(objectModels[i]);
                meshes.get(i).pos.x = xpos[i];
            }
            catch (FileNotFoundException e1) 
            {
                e1.printStackTrace();
                meshes.get(i).tris = new mesh("cube", new Color(155,0,255)).tris;//setting default as cube incase there is an error amongst the files
                meshes.get(i).pos.x = xpos[i];
            }
            catch (IOException e2) 
            {
                e2.printStackTrace();
                meshes.get(i).tris = new mesh("cube", new Color(155,0,255)).tris;
                meshes.get(i).pos.x = xpos[i];
            }
        }
    }

    public static void drawTri(Graphics2D g, Color c, vec3 p1, vec3 p2, vec3 p3) 
    {
        g.setColor(c);
        g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        g.drawLine((int)p2.x, (int)p2.y, (int)p3.x, (int)p3.y);
        g.drawLine((int)p3.x, (int)p3.y, (int)p1.x, (int)p1.y);

    }

    public static void drawFillTri(Graphics2D g, Color c, vec3 p1, vec3 p2, vec3 p3) 
    {
        g.setColor(c);
        int[] xPoints = {(int)p1.x,(int)p2.x,(int)p3.x};
        int[] yPoints = {(int)p1.y,(int)p2.y,(int)p3.y};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    ArrayList<triangle> renderableTris = new ArrayList<>();
    
    vec3 camera = new vec3(0.f,0.f,-12.f);
    vec3 lookDirection = new vec3();
    vec3 camRight = new vec3();

    float deltaTime = 0.f;//time elapsed per frame
    float initialTime = 0.0f;//starting time
    float yaw = 0.f;
    float pitch = 0.f;
    float theta = 1.f;//DEBUG
    float x = 0.f;
    float prevX = x;
    float y = 0.f;
    float prevY = y;
    boolean print = false;//DEBUG
    //input flags
    boolean moveRight, moveLeft, moveForward, moveBackwards, moveUp, moveDown = false;
    boolean lookUp,lookDown,lookLeft,lookRight = false;
    boolean wireFrame = false;

    
    public void update3D(mesh m) 
    {
        theta += 30*deltaTime;
        mat4x4 proj = mat4x4.perspectiveProjection();//perspective projection matrix
        mat4x4 scale = mat4x4.scale(m.scale);//mesh's scale matrix
        mat4x4 rotZYX = mat4x4.mul(mat4x4.rotateX(m.orientation.x),mat4x4.mul(mat4x4.rotateY(m.orientation.y),mat4x4.rotateZ(m.orientation.z)));//mesh's rotation matrix
        mat4x4 trans = mat4x4.translate(m.pos);//mesh's translation matrix

        //placeholder for triangle that gets drawn
        triangle projected = new triangle();

        //local transfrom matrix
        mat4x4 local = mat4x4.mul(trans, mat4x4.mul(rotZYX,scale));//pov java doesn't have operator overloading :(

        //camera matrix
        vec3 up = new vec3(0.f,1.f,0.f);//up direction
        vec3 target = new vec3(0.f,0.f,1.f);//forward direction
        //calculatin rotation from yaw and pitch
        mat4x4 cameraRot = mat4x4.mul(mat4x4.rotateY(yaw),mat4x4.rotateX(pitch));
        //applying the calculated yaw and pitch rotation to look direction
        lookDirection = new vec3(mat4x4.mul(target,cameraRot));//forward direction relativ to camera
        camRight = util.cross(lookDirection,up);//right direction relative to look direction
        camRight.setY(0.f);
        target = lookDirection.add(camera);
        //System.out.println(lookDirection);
        mat4x4 matCamera = mat4x4.lookAt(camera, target, up);

        //applying projection ands transformations to each triangle in a mesh
        for (triangle tri: m.tris) 
        {   
            //TRANSFORM
            //applying local transform to each vertex in the triangle
            for (int i = 0; i < projected.verts.length; i++) 
            {
                projected.verts[i] = new vec3(mat4x4.mul(tri.verts[i], local));
            }

            //calculating normals of each face (a normal is the direction that the face is facing, use the cross product to get a normal)
            vec3 normal, line1, line2;
            line1 = projected.verts[1].subtract(projected.verts[0]);
            line2 = projected.verts[2].subtract(projected.verts[0]);
            normal = util.cross(line2, line1);
            //normalizing the normal (normal is the direction of the face, normalizing it makes its length 1.f units)
            normal.normalize();

            vec3 cameraRay = projected.verts[0].subtract(camera);

            //this if statement checks if the face is facing towards you so it only renders things which you can see
            if (util.dot(normal,cameraRay) > 0.f)
            {
                //calculate the lighting - will affect how bright a face is
                vec3 light = cameraRay;
                light.normalize();
                
                float dp = util.dot(normal,light);//dot product determines the intesity of the light
                //if its pointing straight towards you then its 1 giving the full brightness
                //but if its pointing perpendicular then its 0 since its completely away from you
                dp = Math.abs(dp);
                
                dp = Math.clamp(dp, 0.1f, 1.0f);//makes it easier to see really dark faces/lines

                projected.c = new Color((int)(m.c.getRed()*dp),(int)(m.c.getGreen()*dp),(int)(m.c.getBlue()*dp));

                for (int i = 0; i < projected.verts.length; i++) 
                {
                    projected.verts[i] = new vec3(mat4x4.mul(projected.verts[i], matCamera));
                    //if (print) {System.out.println(matCamera);print = false;}//DEBUG
                }

                //calculating average depth for each triangle so that the triangles can be drawn from back to front
                for (int i = 0; i < projected.verts.length; i++) 
                {
                    projected.avgDepth += projected.verts[i].z;
                }
                projected.avgDepth /= 3.f;

                ArrayList<triangle> clippedTris = util.clipTriangles(new vec3(0.f,0.f,util.nearPlane), new vec3(0.f,0.f,-1.f), projected);

                for (triangle clipTris : clippedTris) 
                {
                    //PROJECTION
                    //applying projection matrix to each vertex in the triangle - This is where the magic happens!!!
                    for (int i = 0; i < clipTris.verts.length; i++) 
                    {
                        vec4 v = new vec4();
                        v = mat4x4.mul(clipTris.verts[i], proj);

                        //setting projection matrix values equal to the current vertex
                        clipTris.verts[i].x = v.x;
                        clipTris.verts[i].y = v.y;
                        clipTris.verts[i].z = v.z;

                        //performing perspective divide on each component of the vertex
                        if (v.w != 0) 
                        {
                            clipTris.verts[i].x /= v.w;
                            clipTris.verts[i].y /= v.w;
                            clipTris.verts[i].z /= v.w;
                        }
                    }

                    //converting vertex positions from normalized space to screen space
                    for (int i = 0; i < clipTris.verts.length; i++) 
                    {
                        clipTris.verts[i].x += 1.f;
                        clipTris.verts[i].y += 1.f;
                        clipTris.verts[i].x *= 0.5f*(float)util.width;
                        clipTris.verts[i].y *= 0.5f*(float)util.height;
                    }


                    //drawing the triangle
                    triangle newTri = new triangle(clipTris.verts,clipTris.c);
                    newTri.avgDepth = tri.avgDepth;

                    renderableTris.add(newTri);

                    //drawFillTri(g, projected.c, projected.verts[0],projected.verts[1],projected.verts[2]);
                    //drawTri(g, projected.c, projected.verts[0],projected.verts[1],projected.verts[2]);
                }
            }
        }
    }

    //rendering the triangles from back to front 
    public void render(Graphics2D g) 
    {

        Collections.sort(renderableTris);
        for (triangle tri : renderableTris) 
        {
            if (!wireFrame){tri.renderTri(g);}
            else {tri.renderWireTri(g);}
        }
        renderableTris.clear();//emptying out the previous triangles
    }


    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    //mouse input
    public void mouseMoved(MouseEvent e) 
    {
        x = -(((e.getX()-util.width/2)));
        y = (((e.getY()-util.height/2)));
        pitch += (y-prevY)*0.1f;
        yaw  += (x-prevX)*0.1f;
        prevX = x;
        prevY = y;
        try 
        {
            //makes the mouse loop around the screen so you can turn inifnitley in any direction(except exceeding the pitch intervals)
            Robot r = new Robot();
            if (-x > util.width/2-2) {r.mouseMove(5,(int)(util.height/2+y)); prevX = util.width/2;}
            else if (-x < -util.width/2+8) {r.mouseMove(util.width-5,(int)(util.height/2+y)); prevX = -util.width/2;}
            if (-y > util.height/2-4) {r.mouseMove((int)(util.width/2-x),util.height); prevY = util.height/2;}
            else if (-y < -util.height/2+5) {r.mouseMove((int)(util.width/2-x),5); prevY = -util.height/2;}

        } 
        catch (AWTException s) 
        {
        }
    }
    
    //better to have input flags as this allows multiple key inputs per frame and also much smoother input
    public void keyPressed(KeyEvent e) 
    {
        print = true;

        //UTILITY KEYS
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { System.exit(0);}//exits program
        if (e.getKeyCode() == KeyEvent.VK_J) {wireFrame = (wireFrame == true) ? false : true;}

        //MOVEMENT KEYS
        //move right and left
        if (e.getKeyCode() == KeyEvent.VK_D) {moveRight = true;}
        if (e.getKeyCode() == KeyEvent.VK_A) {moveLeft = true;}
        //move forwad and back
        if (e.getKeyCode() == KeyEvent.VK_W) {moveForward = true;}
        if (e.getKeyCode() == KeyEvent.VK_S) {moveBackwards = true;}
        //move up and down
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {moveUp = true;}
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {moveDown = true;}
        //look left and right
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {lookLeft = true;}
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {lookRight = true;}
        //look up and down
        if (e.getKeyCode() == KeyEvent.VK_UP) {lookUp = true;}
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {lookDown = true;}
    }
    
    public void keyReleased(KeyEvent e) 
    {
        //move right and left
        if (e.getKeyCode() == KeyEvent.VK_D) {moveRight = false;}
        if (e.getKeyCode() == KeyEvent.VK_A) {moveLeft = false;}
        //move forwad and back
        if (e.getKeyCode() == KeyEvent.VK_W) {moveForward = false;}
        if (e.getKeyCode() == KeyEvent.VK_S) {moveBackwards = false;}
        //move up and down
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {moveUp = false;}
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {moveDown = false;}
        //look left and right
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {lookLeft = false;}
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {lookRight = false;}
        //look up and down
        if (e.getKeyCode() == KeyEvent.VK_UP) {lookUp = false;}
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {lookDown = false;}

    }

    public void keyTyped(KeyEvent e) {}
 
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) 
    {
        Graphics2D s = (Graphics2D)g;

        //calculating elapsed time to allow for frame independant movment
        float t2 = System.nanoTime();//very accurate time but in nano seconds // FOR SOME REASON THIS DOESN'T WORK SOMETIMES
        deltaTime = 1.f/util.refreshRate;//(t2-initialTime)/(float)Math.pow(10, 9);//converting nanoseconds to seconds - divide by 10^9
        initialTime = t2;

        //check for input flags then apply the neccessary movement
        //move controls
        if (moveRight) { camera.increment(camRight.scale(15.0f*deltaTime)); }
        if (moveLeft) { camera.decrement(camRight.scale(15.0f*deltaTime)); }
        if (moveForward)   { camera.increment(new vec3(lookDirection.x,0.f,lookDirection.z).scale(15.0f*deltaTime)); }
        if (moveBackwards) { camera.decrement(new vec3(lookDirection.x,0.f,lookDirection.z).scale(15.0f*deltaTime)); }
        if (moveUp) {camera.y += 12.0f*deltaTime;}
        if (moveDown) {camera.y -= 12.0f*deltaTime;}
        //look controls
        if (lookLeft) {yaw += 50.0f*deltaTime;}
        if (lookRight) {yaw -= 50.0f*deltaTime;}
        if (lookUp) {pitch -= 50.0f*deltaTime;}
        if (lookDown) {pitch += 50.0f*deltaTime;}
        //clamping pitch so camera doesnt do a barrel roll
        if (pitch >= 89.f) {pitch = 89.f;}
        if (pitch <= -89.f) {pitch = -89.f;}
        //looping the yaw values so the angles dont get too big when spinning for a long time
        if (yaw >= 179.f) {yaw = -179.f;}
        else if (yaw <= -179.f) {yaw = 179.f;}


        //a few shenanagins (just applying other transformations to the objects)
        meshes.get(0).orientation = new vec3(0.f,theta,0.f);

        meshes.get(1).pos.y = 5*(float)Math.sin(util.degToRad(theta));
        
        meshes.get(2).pos.x += (float)Math.sin(util.degToRad(theta))/13;
        meshes.get(2).pos.z += (float)Math.cos(util.degToRad(theta))/13;

        meshes.get(3).pos.z = 5*(float)Math.sin(util.degToRad(theta));

        meshes.get(4).orientation = new vec3(theta,theta,0.f);
        meshes.get(4).pos.z += (float)Math.sin(util.degToRad(theta))/5;

        meshes.get(5).orientation = new vec3(0.f,theta,theta);

        meshes.get(6).pos.x += (float)Math.sin(util.degToRad(theta))/13;
        meshes.get(6).pos.z += (float)Math.cos(util.degToRad(theta))/13;
        meshes.get(6).orientation = new vec3(theta,theta,0.f);


        //updating the 3d graphics
        for (mesh m : meshes) 
        {
            update3D(m);
        }
        
        //drawing the triangles
        
        render(s);
        //renderering crosshair
        s.setColor(Color.white);
        s.fillRect(util.width/2, util.height/2, 2, 2);

        //adding delay so program runs at screens refresh rate fps
        try 
        {
            Thread.sleep(1000/(int)util.refreshRate);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

        //repainting
        repaint();
    }
}
