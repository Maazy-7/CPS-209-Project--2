public interface operations// default operations for vectors to perform, add, subtract, scale
{
    vec3 add(vec3 v);
    vec3 subtract(vec3 v);
    vec3 scale(float scalar);
    vec3 invScale(float scalar);
    vec3 normal();
    
    void increment(vec3 v);
    void decrement(vec3 v);
    void normalize();
    float magnitude();
}