import sbtassembly._

class IncludeFromJar(val jarName: String) extends MergeStrategy {

  val name = "includeFromJar"

  def apply(tmpDir: java.io.File, path : scala.Predef.String, files : scala.Seq[java.io.File]) : scala.Either[scala.Predef.String, scala.Seq[scala.Tuple2[java.io.File, scala.Predef.String]]] = {
    val includedFiles = files.flatMap { f =>
      val (source, _, _, isFromJar) = sbtassembly.AssemblyUtils.sourceOfFileForMerge(tmpDir, f)
      if(isFromJar && source.getName == jarName) Some(f -> path) else None
    }
    Right(includedFiles)
  }

}