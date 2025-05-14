import java.util.ArrayList;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

public class mesh 
{
    public ArrayList<triangle> tris = new ArrayList<>();
    Color c;
    vec3 pos = new vec3();
    vec3 orientation = new vec3();
    vec3 scale = new vec3(1.0f,1.0f,1.0f);

    mesh(Color c_) {c = c_;}
    //takes in a shape string then based of the string will generate the vertices for that shape
    mesh(String shape,Color c_) 
    {
        c = c_;
        switch(shape) 
        {
            case "cube":
            //front face
            tris.add(new triangle(new vec3(-1,-1,1), new vec3(1,-1,1), new vec3(-1,1,1)));
            tris.add(new triangle(new vec3(-1,1,1), new vec3(1,-1,1), new vec3(1,1,1)));
            //back face
            tris.add(new triangle(new vec3(-1,-1,-1), new vec3(-1,1,-1), new vec3(1,-1,-1)));
            tris.add(new triangle(new vec3(-1,1,-1), new vec3(1,1,-1), new vec3(1,-1,-1)));
            //left face
            tris.add(new triangle(new vec3(-1,1,1), new vec3(-1,1,-1), new vec3(-1,-1,-1)));
            tris.add(new triangle(new vec3(-1,-1,1), new vec3(-1,1,1), new vec3(-1,-1,-1)));
            //right face
            tris.add(new triangle(new vec3(1,1,1), new vec3(1,-1,-1), new vec3(1,1,-1)));
            tris.add(new triangle(new vec3(1,-1,1), new vec3(1,-1,-1), new vec3(1,1,1)));
            //top face
            tris.add(new triangle(new vec3(-1,1,-1), new vec3(-1,1,1), new vec3(1,1,-1)));
            tris.add(new triangle(new vec3(-1,1,1), new vec3(1,1,1), new vec3(1,1,-1)));
            //bottom face
            tris.add(new triangle(new vec3(-1,-1,-1), new vec3(1,-1,-1), new vec3(-1,-1,1)));
            tris.add(new triangle(new vec3(-1,-1,1), new vec3(1,-1,-1), new vec3(1,-1,1)));

            break;

            case "pyramid":
            //front face
            tris.add(new triangle(new vec3(-1,-1,1), new vec3(0,1,0), new vec3(1,-1,1),c));
            //right face
            tris.add(new triangle(new vec3(1,-1,1), new vec3(0,1,0), new vec3(1,-1,-1),c));
            //left face
            tris.add(new triangle(new vec3(-1,-1,-1), new vec3(0,1,0), new vec3(-1,-1,1),c));
            //back face
            tris.add(new triangle(new vec3(1,-1,-1), new vec3(0,1,0), new vec3(-1,-1,-1),c));
            //bottom face
            tris.add(new triangle(new vec3(-1,-1,-1), new vec3(-1,-1,1), new vec3(1,-1,-1),c));
            tris.add(new triangle(new vec3(-1,-1,1), new vec3(1,-1,1), new vec3(1,-1,-1),c));
            break;
        }
    }

    //loading faces into the mesh using vertex data from the blende file
    public ArrayList<triangle> loadModel (String filename) throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        
        ArrayList<vec3> verts = new ArrayList<>();//vertex list
        ArrayList<triangle> objTris = new ArrayList<>();//face list

        while((line = reader.readLine()) != null) 
        {
            if (line.startsWith("v ")) 
            {
                vec3 vert = new vec3();
                vert.x = Float.valueOf(line.split(" ")[1]);
                vert.y = Float.valueOf(line.split(" ")[2]);
                vert.z = Float.valueOf(line.split(" ")[3]);
                verts.add(vert);//adding vertices
            }

            if (line.startsWith("f ")) 
            {
                int[] f = new int[3];
                //adding face data to array
                f[0] = Integer.valueOf(line.split(" ")[1]);
                f[1] = Integer.valueOf(line.split(" ")[2]);
                f[2] = Integer.valueOf(line.split(" ")[3]);
                //creating the triangle from the vertices by using the face data as indices
                objTris.add(new triangle(verts.get(f[0]-1),verts.get(f[1]-1),verts.get(f[2]-1)));
            }
        }
        reader.close();
        return objTris;
    }

    public void add(triangle tri) 
    {
        tris.add(tri);
    }
}
