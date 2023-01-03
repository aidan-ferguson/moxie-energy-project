using UnityEditor;
using System.Linq;
using System;
using System.IO;

// Adapted from https://gitlab.com/game-ci/unity3d-gitlab-ci-example/-/blob/main/Assets/Scripts/Editor/BuildCommand.cs
static class BuildCommand {
    static void PerformBuild() {
        // Set build target to android
        var buildTarget = BuildTarget.Android;

        Console.WriteLine("[*] Performing build");
        
        // Hardcoded version value for now
        PlayerSettings.bundleVersion = "1.0.0";

        // Set build path to env variable defined in CI file and scripting backend to IL2CPP
        var buildPath = GetBuildPath();
        var buildOptions = BuildOptions.None;
        SetScriptingBackendFromEnv(buildTarget, "IL2CPP");

        var buildReport = BuildPipeline.BuildPlayer(GetEnabledScenes(), buildPath, buildTarget, buildOptions);

        if (buildReport.summary.result != UnityEditor.Build.Reporting.BuildResult.Succeeded)
            throw new Exception($"Build ended with {buildReport.summary.result} status");

        Console.WriteLine(":: Done with build");
    }

    static string[] GetEnabledScenes()
    {
        return (
            from scene in EditorBuildSettings.scenes
            where scene.enabled
            where !string.IsNullOrEmpty(scene.path)
            select scene.path
        ).ToArray();
    }


    static string GetBuildPath() {
        string buildPath = GetArgument("customBuildPath");
        Console.WriteLine(":: Received customBuildPath " + buildPath);
        if (buildPath == "")
        {
            throw new Exception("customBuildPath argument is missing");
        }
        return buildPath;
    }

    static string GetArgument(string name)
    {
        string[] args = Environment.GetCommandLineArgs();
        for (int i = 0; i < args.Length; i++)
        {
            if (args[i].Contains(name))
            {
                return args[i + 1];
            }
        }
        return null;
    }

     static void SetScriptingBackendFromEnv(BuildTarget platform, String backend) {
        var targetGroup = BuildPipeline.GetBuildTargetGroup(platform);
        if (scriptingBackend.TryConvertToEnum(out ScriptingImplementation backend)) {
            Console.WriteLine($":: Setting ScriptingBackend to {backend}");
            PlayerSettings.SetScriptingBackend(targetGroup, backend);
        } else {
            string possibleValues = string.Join(", ", Enum.GetValues(typeof(ScriptingImplementation)).Cast<ScriptingImplementation>());
            throw new Exception($"Could not find '{scriptingBackend}' in ScriptingImplementation enum. Possible values are: {possibleValues}");
        }
    }

    static bool TryConvertToEnum<TEnum>(this string strEnumValue, out TEnum value)
    {
        if (!Enum.IsDefined(typeof(TEnum), strEnumValue))
        {
            value = default;
            return false;
        }

        value = (TEnum)Enum.Parse(typeof(TEnum), strEnumValue);
        return true;
    }


}