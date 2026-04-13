{
  description = "stakeholder-circus kotlin-stakeholder local parity tranche";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      systems = [ "x86_64-linux" "aarch64-darwin" "x86_64-darwin" ];
      forAllSystems = nixpkgs.lib.genAttrs systems;
    in {
      devShells = forAllSystems (system:
        let pkgs = import nixpkgs { inherit system; };
        in {
          default = pkgs.mkShell {
            packages = with pkgs; [ git jq python312 temurin-bin-17 gradle ];
          };
        });
      apps = forAllSystems (system:
        let pkgs = import nixpkgs { inherit system; };
            mk = name: text: {
              type = "app";
              program = "${pkgs.writeShellScript name text}";
            };
        in {
          build = mk "build" ''./gradlew --no-daemon build'';
          test = mk "test" ''./gradlew --no-daemon test'';
          check = mk "check" ''python3 scripts/validate_scaffold.py && ./gradlew --no-daemon ktlintCheck test'';
          format = mk "format" ''./gradlew --no-daemon ktlintFormat'';
        });
    };
}
