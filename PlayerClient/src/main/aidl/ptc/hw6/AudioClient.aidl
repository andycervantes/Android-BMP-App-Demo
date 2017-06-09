// AudioClient.aidl
package ptc.hw6;

// Declare any non-default types here with import statements
// used for the communication of two different applications
// heavily influenced by the google api/developers documentation/examples
interface AudioClient {

    // functions for the interface
    boolean stop();
    boolean play(int number);
    boolean idle();
    boolean pause();

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
}
