import java.awt.Toolkit;
import java.util.ArrayList;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;

public class util //class to store constants and utility operations and whatnot deg to rad, distance, normal, dot product, cross product, etc
{

    //constants
    final static int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    final static int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    //long ah line, gets refresh rate of the device but clamps it to a minium of 60hz and maximum of 240hz
    final static float refreshRate = Math.clamp(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getRefreshRate(), 60.f, 240.f);
    

    final static float farPlane = 1000.f;
    final static float nearPlane = 0.1f;
    
    final static float fov = 90.f;
    final static float aspectRatio = (float)width/(float)height;

    //utility functions
    public static float degToRad(float theta) //converts degree to radians
    {
        return theta * (float)(Math.PI/180);
    }

    public static float radToDeg(float theta) //converts radians to degrees
    {
        return theta * (float)(180/Math.PI);
    }

    public static float dot(vec3 v1, vec3 v2) //dot product of 2 vectors
    {
        return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
    }

    public static float dot(vec4 v1, vec4 v2) 
    {
        return dot(new vec3(v1), new vec3(v2));
    }

    public static vec3 cross(vec3 v1, vec3 v2) //cross product of 2 vectors
    {
        vec3 res = new vec3();
        res.x = v1.y*v2.z - v1.z*v2.y;
        res.y = v1.z*v2.x - v1.x*v2.z;
        res.z = v1.x*v2.y - v1.y*v2.x;
        return res;
    }

    public static vec3 cross(vec4 v1, vec4 v2) 
    {
        return cross(new vec3(v1),new vec3(v2));
    }

    public static float distance(vec3 v1, vec3 v2) 
    {
        return (float)Math.sqrt((v2.x-v1.x)*(v2.x-v1.x) + (v2.y-v1.y)*(v2.y-v1.y) + (v2.z-v1.z)*(v2.z-v1.z));
    }

    public static float distance(vec4 v1, vec4 v2) 
    {
        return distance(new vec3(v1), new vec4(v2));
    }

    public static float signedDistance(vec3 p, vec3 plane_n, vec3 plane_p) 
    {
        return util.dot(plane_n,p)-util.dot(plane_n,plane_p);
    }
    public static vec3 intersectLinePlane(vec3 p1, vec3 p2, vec3 plane_p, vec3 plane_n) 
    {
        plane_n.normalize();
        float planeDot = util.dot(plane_n,plane_p);
        float ad = util.dot(p1,plane_n);
        float bd = util.dot(p2,plane_n);
        float t = (planeDot-ad)/(bd-ad);
        vec3 line = p2.subtract(p1);
        vec3 res = line.scale(t);
        return p1.add(res);
    }

    public static ArrayList<triangle> clipTriangles(vec3 plane_n, vec3 plane_p, triangle face) 
    {
        ArrayList<triangle> res = new ArrayList<>();
        plane_n.normalize();

        ArrayList<vec3> insidePoints = new ArrayList<>();
        ArrayList<vec3> outsidePoints = new ArrayList<>();

        //checking if points lie on inside of the plane
        float d0 = signedDistance(face.verts[0], plane_n, plane_p);
        float d1 = signedDistance(face.verts[1], plane_n, plane_p);
        float d2 = signedDistance(face.verts[2], plane_n, plane_p);
        
        //greater than or equal to 0 results in the point being on the inside of the plane otherwise its on the outside
        if (d0 >= 0) {insidePoints.add(face.verts[0]);}
        else {outsidePoints.add(face.verts[0]);}
        if (d1 >= 1) {insidePoints.add(face.verts[1]);}
        else {outsidePoints.add(face.verts[1]);}
        if (d2 >= 2) {insidePoints.add(face.verts[2]);}
        else {outsidePoints.add(face.verts[2]);}

        //case 1 all vertices of triangle are in the screen
        if (insidePoints.size() == 3) 
        {
            res.add(face);//just add the original triangle
        }

        //case 2 two points are outside the screen, make 1 new triangle
        if (insidePoints.size() == 1 && outsidePoints.size() == 2) 
        {
            triangle newTri = new triangle();
            newTri.c = face.c;
            newTri.verts[0] = insidePoints.get(0);
            newTri.verts[1] = intersectLinePlane(insidePoints.get(0), outsidePoints.get(0), plane_p, plane_n);
            newTri.verts[2] = intersectLinePlane(insidePoints.get(0), outsidePoints.get(1), plane_p, plane_n);
            res.add(newTri);
        }

        //case 3 one point is outside the screen, make 2 new triangles
        if (insidePoints.size() == 2 && outsidePoints.size() == 1) 
        {
            triangle newTri = new triangle();
            triangle newtri2 = new triangle();
            newTri.c = face.c;
            newtri2.c = face.c;

            newTri.verts[0] = insidePoints.get(0);
            newTri.verts[1] = insidePoints.get(1);
            newTri.verts[2] = intersectLinePlane(insidePoints.get(0), outsidePoints.get(0), plane_p, plane_n);
            newtri2.verts[0] = insidePoints.get(1);
            newtri2.verts[1] = newTri.verts[2];
            newTri.verts[2] = intersectLinePlane(insidePoints.get(1), outsidePoints.get(0), plane_p, plane_n);

            res.add(newTri);
            res.add(newTri);
        }

        return res;
    }
}
