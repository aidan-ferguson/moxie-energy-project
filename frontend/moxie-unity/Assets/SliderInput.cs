using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class SliderInput : MonoBehaviour
{
    [SerializeField] Slider slider;
    [SerializeField] float speed;
    void Update()
    {
        slider.value += Input.mouseScrollDelta.y * speed * Time.deltaTime;
    }
}
