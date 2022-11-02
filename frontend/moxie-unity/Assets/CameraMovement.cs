using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CameraMovement : MonoBehaviour
{
    public Transform cameraTransform;
    public float panSpeed;
    public float zoomSpeed;

    public Vector2 zoomDistance;
    float zoomTarget;

    public Vector2 panDuration;

    float panSequenceLength;
    float panSequenceStart;
    float zoomSequenceStart;

    float zDist;
    float rot;

    public int panDir;
    float panSmooth;
    public float panSmoothAmount;

    void Update()
    {
        if(Mathf.Abs(cameraTransform.localPosition.z - zoomTarget) <= 0.1)
        {
            zoomTarget = Random.Range(zoomDistance.x, zoomDistance.y);
        }
        else
        {
            DoZoom();
        }

        if (Time.time > panSequenceStart + panSequenceLength)
        {
            panSequenceLength = Random.Range(panDuration.x, panDuration.y);
            panSequenceStart = Time.time;
            if(panDir == -1)
            {
                panDir = 1;
            }
            else
            {
                panDir = -1;
            }
        }
        else
        {
            DoPan();
        }
    }

    void DoZoom()
    {
        zDist = Mathf.Lerp(zDist, zoomTarget, zoomSpeed * Time.deltaTime);
        cameraTransform.localPosition = new Vector3(0, 0, zDist);
    }

    void DoPan()
    {
        panSmooth = Mathf.Lerp(panSmooth, panDir, panSmoothAmount * Time.deltaTime);
        rot = panSpeed * Time.deltaTime * panSmooth;
        transform.localEulerAngles += Vector3.up * rot;
    }
}
