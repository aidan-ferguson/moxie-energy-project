using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class spin : MonoBehaviour
{
    private bool should_spin = true;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        if(should_spin) {
            transform.Rotate(22 * Time.deltaTime, 50 * Time.deltaTime, 10 * Time.deltaTime);
        }
    }

    public void ToggleSpin(string new_y)
    {
        should_spin = !should_spin;
        //transform.position = new Vector3(transform.position.x, float.Parse(new_y), transform.position.z);
    }
}
