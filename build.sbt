import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}



lazy val baseSettings = Seq(
  scalaVersion := "2.12.6",
  version := "0.1",
  scalacOptions ++= Seq(
    //"-deprecation",
    "-feature"
  ),
  javacOptions ++= Seq("-encoding", "UTF-8")
)


lazy val core =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("core"))
    .settings(
      name := "hellocross",
      baseSettings
    )
    .settings(
      libraryDependencies ++= Seq("com.chuusai" %%% "shapeless" % "2.3.3")
    )
    .jvmSettings()
    .jsSettings()

// Needed, so sbt finds the projects
lazy val coreJVM = core.jvm
lazy val coreJS = core.js




lazy val exampleInJVM = project.in(file("example/inJVM"))
  .dependsOn(coreJVM)
  .settings(
    name := "JvmExample",
    baseSettings
  )


lazy val exampleInJS = project.in(file("example/inJS"))
  .dependsOn(coreJS)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "JsExample",
    baseSettings
  )
  .settings(
    scalaJSUseMainModuleInitializer := true,
    inConfig(Compile)(
      Seq(
        fullOptJS,
        fastOptJS,
        packageJSDependencies,
        packageMinifiedJSDependencies
      ).map(f => (crossTarget in f) ~= (_ / "sjsout"))
    ))
  .settings(skip in packageJSDependencies := false)
















