"""Validate converted recipe item IDs against Minecraft 26.3's actual client assets."""
import argparse
import hashlib
import json
from pathlib import Path
import urllib.request
import zipfile


def vanilla_jar(cache):
    cache.mkdir(parents=True, exist_ok=True)
    with urllib.request.urlopen('https://piston-meta.mojang.com/mc/game/version_manifest_v2.json', timeout=30) as response:
        manifest = json.load(response)
    version_url = next(v['url'] for v in manifest['versions'] if v['id'] == '26.3')
    with urllib.request.urlopen(version_url, timeout=30) as response:
        version = json.load(response)
    client = version['downloads']['client']
    target = cache / 'minecraft-26.3-client.jar'
    if not target.exists() or hashlib.sha1(target.read_bytes()).hexdigest() != client['sha1']:
        urllib.request.urlretrieve(client['url'], target)
    if hashlib.sha1(target.read_bytes()).hexdigest() != client['sha1']:
        raise ValueError('Minecraft client download failed its published SHA-1 check')
    return target


def check(resources, minecraft_jar):
    with zipfile.ZipFile(minecraft_jar) as archive:
        vanilla = {'minecraft:' + n[len('assets/minecraft/items/'):-5]
                   for n in archive.namelist()
                   if n.startswith('assets/minecraft/items/') and n.endswith('.json')}
    if not vanilla:
        raise ValueError('Provided Minecraft JAR contains no client item definitions')
    custom = {'clay:' + p.relative_to(resources / 'assets/clay/items').with_suffix('').as_posix()
              for p in (resources / 'assets/clay/items').rglob('*.json')}
    known = vanilla | custom
    errors = []

    def ingredient_ids(value):
        if isinstance(value, str):
            if not value.startswith('#'):
                yield value
        elif isinstance(value, list):
            for entry in value:
                yield from ingredient_ids(entry)
        else:
            raise ValueError('Unexpected converted ingredient: ' + repr(value))

    recipes = sorted((resources / 'data/clay/recipe').rglob('*.json'))
    if not recipes:
        raise ValueError('No converted recipes found')
    for path in recipes:
        data = json.loads(path.read_text(encoding='utf-8'))
        ingredients = list(data['key'].values()) if 'key' in data else data['ingredients']
        ids = list(ingredient_ids(ingredients)) + [data['result']['id']]
        for item in ids:
            if item not in known:
                errors.append(f'{path.relative_to(resources)}: unknown item {item}')
    if errors:
        raise ValueError('\n'.join(errors))
    print(f'Validated all item references in {len(recipes)} recipes against {len(vanilla)} vanilla and {len(custom)} Clay item definitions.')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--resources', type=Path, default=Path('build/migration-26.3/resources'))
    parser.add_argument('--minecraft-jar', type=Path)
    args = parser.parse_args()
    check(args.resources, args.minecraft_jar or vanilla_jar(Path('build/recipe-validation')))
