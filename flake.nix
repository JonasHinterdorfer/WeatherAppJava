{
  description = "Java development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    common.url = "github:JonasHinterdorfer/flake-templates?dir=common";
  };

  outputs = { self, nixpkgs, common }:
    let
      forAllSystems = common.lib.forAllSystems nixpkgs;
    in
    {
      packages = forAllSystems (system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
        in
        {
          default = pkgs.stdenv.mkDerivation {
            pname = "myapp";
            version = "0.1.0";
            src = ./.;
            nativeBuildInputs = [ pkgs.maven ];
            buildPhase = ''
              mvn package
            '';
            installPhase = ''
              mkdir -p $out/bin
              cp target/*.jar $out/bin/
            '';
          };
        });

      devShells = forAllSystems (system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
        in
        {
          default = pkgs.mkShell {
            packages = with pkgs; [
              jdk
              maven
              quarkus
              gradle
              jdt-language-server
            ];

            shellHook = ''
              echo "Java development environment loaded"
              echo "Java version: $(java -version 2>&1 | head -n 1)"
              echo "Maven version: $(mvn --version | head -n 1)"
              echo "Gradle version: $(gradle --version | grep Gradle)"
              
              ${common.lib.interactiveDirenvPrompt}
            '';
          };
        });
    };
}
