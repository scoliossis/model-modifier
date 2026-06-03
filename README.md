# Model Modifier for Minecraft Fabric 1.21.11
I'll write a cool readme later cause i like this project quite a bit

## Example Video: https://youtu.be/nQAgm74q6ck

### Features:
- Works with all minecraft effects, such as glowing and invisibility
- Works with minecraft smooth lighting and shading packs
- Allows you to modify the model of any entity via a texture pack [example packs](https://github.com/scoliossis/model-modifier/tree/master/example-resource-packs)
- The models are only rendered on real players and not on server bots

![glowing%20and%20items.png](repo/glowing%20and%20items.png)

### Todo:
- the demo models are frankly FAR too big, and therefore have quite a hefty fps impact
- write a cooler readme
- make a guide video on how to make models
- it currently hides all armour, think of a smart way to render other armour without looking weird
- models are drawn using quads with three different vertices, this is SLOWER than using triangles
- code is far too messy for something so simple
- add extra options to the config json, like force glow/invis

### Create an issue if you find any bugs or have any suggestions!