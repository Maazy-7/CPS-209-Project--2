public class vec3 implements operations
{
    protected float x,y,z;//protected so children can access it

    vec3() {x = 0; y = 0; z = 0;}//default vec3 with all values set to 0

    vec3(float x_, float y_, float z_)//normal vec3 creation
    {
        x = x_;
        y = y_;
        z = z_;
    }

    vec3(vec4 v) {x = v.x; y = v.y; z = v.z;}//takes in vec4 and applies its x,y and z components only

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public vec3 add(vec3 v) //vec3 + vec3
    {
        return new vec3(x+v.getX(), y+v.getY(), z+v.getZ());
    }

    public vec3 subtract(vec3 v) //vec3 - vec3
    {
        return new vec3(x-v.getX(),y-v.getY(),z-v.getZ());
    }

    public vec3 scale(float scalar) //vec3 * float
    {
        return new vec3(x*scalar, y*scalar, z*scalar);
    }

    public vec3 invScale(float scalar)//vec3 / float 
    {
        return new vec3(x/scalar, y/scalar, z/scalar);
    }

    //assignment operators
    public void increment(vec3 v) //+= operator
    {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void decrement(vec3 v) //-= operator
    {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    //useful operations
    public float magnitude() //returns magnitude of the vector
    {
        return util.distance(this, new vec3());//distance of the vector from this to (0,0,0) which is the magntiude
    }

    public void normalize()//normalizes the vector (makes the vector's lenght equal to 1 while maintaining the same direction - unit vector)
    {
        float d = this.magnitude();
        x /= d;
        y /= d;
        z /= d;
    }

    public vec3 normal() //returns the normal of the vector
    {
        float d = util.distance(this, new vec3());
        return new vec3(this.x/d,this.y/d,this.z/d);
    }

    @Override
    public String toString() 
    {
        String s = "";
        s+= "(" + Math.round(x*100.f)/100.f + ","+ Math.round(y*100.f)/100.f + "," + Math.round(z*100.f)/100.f + ")";
        return s;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) {return true;}
        if (!(obj instanceof vec3))//object isnt vec3 but could be vec4
        {
            if (obj instanceof vec4) //checking if object is vec4
            {
                vec4 v = (vec4)obj;// comparing x,y,z of vec4 to this vec3's x,y,z
                return v.x == this.x && v.y == this.y && v.z == this.z;
            }
            else {return false;}//object is not vec3 or vec4
        }
        vec3 v = (vec3)obj;
        return v.x == this.x && v.y == this.y && v.z == this.z;
    }

}
