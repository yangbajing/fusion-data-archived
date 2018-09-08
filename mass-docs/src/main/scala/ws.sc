import java.nio.file.Paths
Set(1, 2, 3, 4) sameElements Set(2, 3, 1, 4)

Set(1, 2, 3, 4) == Set(2, 3, 1, 4)
Set(1, 2, 3, 4) equals Set(2, 3, 1, 4)


val relativePath = Paths.get("a4", "923843298432")

Paths.get("/opt/").resolve(relativePath)