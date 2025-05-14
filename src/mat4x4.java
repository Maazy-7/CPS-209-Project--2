public class mat4x4 // 4 by 4 matrix class (4x4 matrices commonly used for 3d rendering)
{
    private float[][] m = new float[4][4];

    //constructors and matrix creation
    mat4x4() 
    {
        m[0][0] = 1.f;
        m[1][1] = 1.f;
        m[2][2] = 1.f;
        m[3][3] = 1.f;
    }

    mat4x4(float identity)//constructing identity matrix(technically a scale matrix if identity isn't 1... oh well)
    {
        m[0][0] = identity;
        m[1][1] = identity;
        m[2][2] = identity;
        m[3][3] = identity;
    }

    //identity matrix - basically a matrix where a diagonal from left to right is 
    public static mat4x4 identity() 
    {
        return new mat4x4(1.f);
    }

    //matrix operations
    public static mat4x4 mul(mat4x4 m1, mat4x4 m2)//matrix multiplication 
    {
        mat4x4 res = new mat4x4();//res is result and m1 and m2 are the 2 matrix's being multiplied
        for (int i = 0; i < 4; i++) 
        {
            for (int j = 0; j < 4; j++) 
            {
                res.m[i][j] = m1.m[i][0] * m2.m[0][j] + 
                              m1.m[i][1] * m2.m[1][j] + 
                              m1.m[i][2] * m2.m[2][j] + 
                              m1.m[i][3] * m2.m[3][j];
            }
        }
        return res;
    }

    public static vec4 mul(vec4 v, mat4x4 m1) //vector to matrix multiplication
    {
        vec4 res = new vec4();//resulting matrix
        //no for loop since vec4 cant be iterated through
        res.x = m1.m[0][0] * v.x + m1.m[0][1] * v.y + m1.m[0][2] * v.z + m1.m[0][3] * v.w;
        res.y = m1.m[1][0] * v.x + m1.m[1][1] * v.y + m1.m[1][2] * v.z + m1.m[1][3] * v.w;
        res.z = m1.m[2][0] * v.x + m1.m[2][1] * v.y + m1.m[2][2] * v.z + m1.m[2][3] * v.w;
        res.w = m1.m[3][0] * v.x + m1.m[3][1] * v.y + m1.m[3][2] * v.z + m1.m[3][3] * v.w;

        return res;
    }

    public static vec4 mul(vec3 v, mat4x4 m1) //cast vec3 input to vec4
    {
        return mat4x4.mul(new vec4(v), m1);
    }

    public static boolean equals(mat4x4 m1, mat4x4 m2) //checks if 2 matrices are equal to each other
    {
        for (int i = 0; i < 4; i++) 
        {
            for (int j = 0; j < 4; j++) 
            {
                if(m1.m[i][j] != m2.m[i][j]) {return false;}
            }
        }
        return true;
    }

    //matrix transformations - scale, rotate and translation

    public static mat4x4 scale(float s) // returns a scale matrix
    {
        mat4x4 res = new mat4x4();
        res.m[0][0] = s;
        res.m[1][1] = s;
        res.m[2][2] = s;
        res.m[3][3] = 1;
        return res;
    }

    public static mat4x4 scale(float sx, float sy, float sz) // returns a scale matrix with x,y,z inputs for scaling
    {
        mat4x4 res = new mat4x4();
        res.m[0][0] = sx;
        res.m[1][1] = sy;
        res.m[2][2] = sz;
        res.m[3][3] = 1;
        return res;
    }

    public static mat4x4 scale(vec3 v) // returns a scale matrix with vector as input
    {
        return mat4x4.scale(v.x,v.y,v.z);
    }

    public static mat4x4 translate(vec3 t) 
    {
        mat4x4 res = new mat4x4(1.f);
        res.m[0][3] = t.x;
        res.m[1][3] = t.y;
        res.m[2][3] = t.z;
        res.m[3][3] = 1;
        return res;
    }

    public static mat4x4 rotateX(float theta) //rotation matrix for x axis
    {
        theta = util.degToRad(theta);
        float c = (float)Math.cos(theta);//calculating trig values before hand so that they are only calculated once
        float s = (float)Math.sin(theta);
        mat4x4 res = new mat4x4();
        res.m[0][0] = 1;
        res.m[1][1] = c;
        res.m[1][2] = -s;
        res.m[2][1] = s;
        res.m[2][2] = c;
        res.m[3][3] = 1;
        return res;
    }

    public static mat4x4 rotateY(float theta) //rotation matrix for y axis
    {
        theta = util.degToRad(theta);
        float c = (float)Math.cos(theta);//calculating trig values before hand so that they are only calculated once
        float s = (float)Math.sin(theta);
        mat4x4 res = new mat4x4();
        res.m[0][0] = c;
        res.m[1][1] = 1;
        res.m[0][2] = s;
        res.m[2][0] = -s;
        res.m[2][2] = c;
        res.m[3][3] = 1;
        return res;
    }

    public static mat4x4 rotateZ(float theta) //rotation matrix for z axis
    {
        theta = util.degToRad(theta);
        float c = (float)Math.cos(theta);//calculating trig values before hand so that they are only calculated once
        float s = (float)Math.sin(theta);
        mat4x4 res = new mat4x4();
        res.m[0][0] = c;
        res.m[0][1] = -s;
        res.m[1][0] = s;
        res.m[1][1] = c;
        res.m[2][2] = 1;
        res.m[3][3] = 1;
        return res;
    }

    public static mat4x4 perspectiveProjection(float aspectRatio, float farPlane, float nearPlane, float fov) 
    {
        //this is the perspective projection matrix which will bascially allow converting a 3d point to a 2d point
        mat4x4 res = new mat4x4(1.f);
        float tan = (float)(1.f/ Math.tan(util.degToRad(fov/2)));
        float recip = 1.f/(farPlane - nearPlane);
        res.m[0][0] = tan * (1.f/aspectRatio);
        res.m[1][1] = tan;
        res.m[2][2] = -(farPlane+nearPlane)*recip;
        res.m[2][3] = -(2.f*farPlane*nearPlane)*recip;
        res.m[3][2] = -1;
        res.m[3][3] = 0;

        return res;
    }

    public static mat4x4 perspectiveProjection() 
    {
        return mat4x4.perspectiveProjection(util.aspectRatio, util.farPlane, util.nearPlane, util.fov);
    }

    public static mat4x4 lookAt(vec3 pos, vec3 target, vec3 up) 
    {
        mat4x4 res = new mat4x4(0.f);

        //calculate forward direction
        vec3 forward = target.subtract(pos);
        forward.normalize();

        //calculate up direction
        vec3 a = forward.scale(util.dot(up,forward));
        vec3 newUp = up.subtract(a);
        newUp.normalize();

        //calculate right direction
        vec3 right = util.cross(newUp, forward);

        
        //setting the matrix values for the look at matrix
        res.m[0][0] = right.x;	    res.m[0][1] = right.y;	    res.m[0][2] = right.z;	    res.m[0][3] = -util.dot(right, pos);
		res.m[1][0] = newUp.x;		res.m[1][1] = newUp.y;		res.m[1][2] = newUp.z;		res.m[1][3] = -util.dot(newUp, pos);
		res.m[2][0] = forward.x;	res.m[2][1] = forward.y;	res.m[2][2] = forward.z;	res.m[2][3] = -util.dot(forward, pos);
		res.m[3][0] = 0.f;		    res.m[3][1] = 0.f;		    res.m[3][2] = 0.f;		    res.m[3][3] = 1.0f;

        return res;
    }

    //override methods
    @Override
    public String toString() //prints the matrix
    {
        String res = "";

        for (int i = 0; i < 4; i++)// 4 is the length of both arrays in the matrix since its a 4x4 matrix 
        {
            res+= "[";
            for (int j = 0; j < 4; j++) 
            {
                if (m[i][j] >= 0.f) {res += " ";} else {res += "";}//formatting to handle the negative
                res += Math.round(m[i][j]*10.f)/10.f;//rounds to 1 decimal place
                if (j != 3) {res += " ";}
            }
            res += " ]";
            res += "\n";
        }
        return res;
    }
}
