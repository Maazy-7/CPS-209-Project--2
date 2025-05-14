public class Project2Runner {
    
    /*
     * Name: <Maaz Salman>
     * Student ID: <501316684>
     * 
     * maaz.salman@torontomu.ca
     ******** Project Description ********
     * 
     * Describe in plain English the overall program/program in a paragraph or 2.
     * 
     * The primary goal of my program is a 3D renderer using java swing to draw stuff to the screen.
     * The program itself involves a lot of math which is carried over from my previous project #1 and
     * expands on it by applying these matrix transformations in a useful way. My program can take in .obj files which hold
     * vertex and face data of 3d models and can then use it to create and render objects in 3d. The render supports backface culling,
     * lighting, transformations such as rotation, translation, and scaling, it also includes a movable and look around type of camera and
     * applies a simple lighting to objects.
     *
     * 
     ******** Swing Requirement ********
     * 
     * Describe in 1 paragraph how your program satisfies the requirement that
     * there is at least 3 unique components. Be clear to identify in what
     * files and the lines number (just the starting line is fine) that the
     * components are defined on.
     * 
     * one of the components i use is from the Robot component which is defined at window.java line 282
     * another one of the components I used is the ToolKit component which is defined at util.java line 10
     * another component I used is the image component which is defined at window.java line 38
     * 
     * 
     ******** 2D Graphics Requirement ********
     *
     * Describe in 1 paragraph how your program satisfies the requirement that
     * there is at least 1 JPanel used for drawing something. Be clear to
     * identify in what files and the line numbers that this panel is defined on.
     * 
     * At window.java line 85 I have 2 drawing functions called drawFill tri which draws a filled triangle using the graphics 2d
     * drawFillPolygon function. I also have another function called drawTri which draws 3 lines connected together to 
     * form a triangle called drawTri. These are used in triangle.java line 43 to draw the triangles. The actual drawing of these triangles are called
     * in the render function in window.java line 249.
     * 
     * 
     ******** Event Listener Requirement ********
     *
     * Describe in 1 paragraph how your program satisfies the requirement that
     * there is at least one ActionListener, and there is additionally at least
     * one MouseListener or ActionListener. Be clear to identify in what file
     * and the line numbers that these listeners are defined in.
     * 
     * My program uses keybaord and mouse input to move a camera around. The controls fro this movement is as follows
     * W to move forward S to move back A to move left D to move right (left ctr) to move down and (left shift) to move up
     * and move mouse up to look up and move mouse down to look down, these inputs are defined at window.java line 271
     * The keyboard input applies a vector direction and speed to the cameras position while the mouse input calculates a 
     * yaw and pitch for the camera.
     */

    public static void main(String[] args) {
        window t = new window();
    }
}
