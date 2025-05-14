import java.awt.Color;
import java.awt.Graphics2D;

public class triangle implements Comparable<triangle>
{
    vec3[] verts = new vec3[3];//each triangle has 3 vertices
    float avgDepth;// = (verts[0].z + verts[1].z + verts[2].z)/3;
    Color c;

    triangle() 
    {
        verts[0] = new vec3();
        verts[1] = new vec3();
        verts[2] = new vec3();
        avgDepth = 0;
    }

    triangle(vec3 v1, vec3 v2, vec3 v3) 
    {
        verts[0] = v1;
        verts[1] = v2;
        verts[2] = v3;
        avgDepth = (v1.z+v2.z+v3.z)/3.f;//averaging out the z depth
        c = Color.YELLOW;
    }

    triangle(vec3 v1, vec3 v2, vec3 v3,Color c_) 
    {
        verts[0] = v1;
        verts[1] = v2;
        verts[2] = v3;
        c = c_;
    }

    triangle(vec3[] v,Color c_) 
    {
        verts[0] = v[0];
        verts[1] = v[1];
        verts[2] = v[2];
        c = c_;
    }

    public void renderTri(Graphics2D g) 
    {
        window.drawFillTri(g, c, verts[0], verts[1], verts[2]);
    }

    public void renderWireTri(Graphics2D g) 
    {
        window.drawTri(g, c, verts[0], verts[1], verts[2]);
    }

    //used to sort triangle
    @Override
    public int compareTo(triangle o) 
    {
        if (this.avgDepth > o.avgDepth) {return 1;}
        else if (this.avgDepth < o.avgDepth) {return -1;}
        else {return 0;}
    }
    
}
