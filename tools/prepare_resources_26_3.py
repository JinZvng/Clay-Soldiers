"""Stage Clay Soldiers resources for the 26.3 port; does not produce a mod JAR.

Run from the repository root: python3 tools/prepare_resources_26_3.py
The original resource tree is left intact. An existing output is never overwritten.
References:
https://wiki.geckolib.com/docs/geckolib5/updating/important/asset-paths/
https://docs.neoforged.net/primer/docs/1.21.4/
"""
import argparse
import json
import re
import shutil
from pathlib import Path


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2, ensure_ascii=False) + '\n', encoding='utf-8')


def ingredient(value):
    if isinstance(value, str):
        return value
    if isinstance(value, list):
        return [ingredient(v) for v in value]
    if isinstance(value, dict) and set(value) == {'item'}:
        return value['item']
    if isinstance(value, dict) and set(value) == {'tag'}:
        return '#' + value['tag']
    raise ValueError('Unsupported ingredient: ' + repr(value))


def prepare(root, output):
    source = root / 'common/src/main/resources'
    if not source.is_dir():
        raise ValueError('Run against the Clay Soldiers repository root')
    if output.exists():
        raise ValueError('Output already exists; choose a new --output directory')
    source = source.resolve()
    output = output.resolve()
    if output == source or source in output.parents or output in source.parents:
        raise ValueError('Output must be separate from the source tree')
    shutil.copytree(source, output)
    recipes = output / 'data/clay/recipes'
    recipes.rename(output / 'data/clay/recipe')
    count = 0
    for path in sorted((output / 'data/clay/recipe').rglob('*.json')):
        data = json.loads(path.read_text(encoding='utf-8'))
        if data['type'] == 'minecraft:crafting_shapeless':
            data['ingredients'] = [ingredient(v) for v in data['ingredients']]
        elif data['type'] == 'minecraft:crafting_shaped':
            data['key'] = {k: ingredient(v) for k, v in data['key'].items()}
        else:
            raise ValueError('Unsupported recipe type: ' + data['type'])
        if 'item' in data['result']:
            data['result']['id'] = data['result'].pop('item')
        write_json(path, data)
        count += 1
    (output / 'data/clay/tags/items').rename(output / 'data/clay/tags/item')
    assets = output / 'assets/clay'
    (assets / 'geckolib').mkdir()
    (assets / 'geo').rename(assets / 'geckolib/models')
    (assets / 'animations').rename(assets / 'geckolib/animations')

    # Modern client items replace the legacy item color-handler registration.
    # Read the original registry so variant colors are retained exactly.
    registry = (root / 'common/src/main/java/com/matthewperiut/clay/registry/ItemRegistry.java').read_text(encoding='utf-8')
    ids = dict(re.findall(r'(\w+)\s*=\s*register(?:Item|HorseDollItem|MiscItem)\("([^"]+)"', registry))
    colors = {}
    for variable, color in re.findall(r'ClientInfoStorage\.add\((\w+),\s*(0x[0-9A-Fa-f]+)\)', registry):
        if variable not in ids:
            raise ValueError('Unresolved colored item: ' + variable)
        colors[ids[variable]] = int(color, 16) & 0xFFFFFF
    models = 0
    for model in sorted((assets / 'models/item').rglob('*.json')):
        item_id = model.relative_to(assets / 'models/item').with_suffix('').as_posix()
        definition = {'type': 'minecraft:model', 'model': 'clay:item/' + item_id}
        if item_id in colors:
            definition['tints'] = [{'type': 'minecraft:constant', 'value': colors[item_id]}]
        write_json(assets / 'items' / (item_id + '.json'), {'model': definition})
        models += 1
    for path in output.rglob('*.json'):
        json.loads(path.read_text(encoding='utf-8'))
    print(f'Staged {count} recipes, {models} client items, {len(colors)} item colors.')
    print(f'Resources: {output}')
    print('Java API migration and in-game validation remain required. This is not a playable mod.')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    root = args.root.resolve()
    prepare(root, args.output or root / 'build/migration-26.3/resources')
