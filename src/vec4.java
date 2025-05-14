//vec4 exists for the sake of being able to multipliy a vector by a matrix
public class vec4 extends vec3
{
    protected float x,y,z,w = 1.f;
    
    vec4() {x = 0; y = 0; z = 0;}

    vec4(float x_, float y_, float z_) 
    {
        x = x_;
        y = y_;
        z = z_;
    }
    vec4(vec3 v) 
    {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    @Override
    public String toString() 
    {
        String s = "";
        s += "(" + x + ","+ y + "," + z + "," + w + ")";
        return s;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) {return true;}
        if (!(obj instanceof vec4))//object isnt vec4 but could be vec3 
        {
            if (obj instanceof vec3) 
            {
                vec3 v = (vec3)obj;
                return v.x == x && v.y == y && v.z == z;
            }
            else {return false;}//object not vec3 or vec4
        }
        vec4 v = (vec4)obj;
        return v.x == x && v.y == y && v.z == z; 
    }
}