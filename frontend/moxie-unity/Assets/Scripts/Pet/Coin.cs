using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Coin : MonoBehaviour
{
    float min = 3;
    float max = 7;

    bool isActive = true;

    float wait;

    float rotSpeed = 65;

    //[SerializeField]
    //float bobSpeed = 5f;

    //[SerializeField]
    //float bobAmount = 200f;

    [SerializeField] Transform visual;
    void OnTriggerEnter(Collider other)
    {
        if (isActive)
        {
            if (other.transform.name == "Pet")
            {
                isActive = false;
                visual.gameObject.SetActive(false);
                wait = Random.Range(min, max);
                StartCoroutine(Reset(wait));
            }
        }
    }

    IEnumerator Reset(float wait)
    {
        yield return new WaitForSeconds(wait);
        isActive = true;
        visual.gameObject.SetActive(true);
        StopAllCoroutines();
    }
    
    private void Update()
    {
        transform.localEulerAngles += new Vector3(0, rotSpeed * Time.deltaTime, 0);
        transform.localPosition += new Vector3(0, Mathf.Cos(Time.time * 8f) * 0.01f, 0);
    }
}
