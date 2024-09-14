## 4.0.2

- Fix accidental overwriting of existing colour providers
- Fix custom model loading

## 4.0.1

- Improve ET compat
- Fix non-trim-type predicates being overwritten
- Rudamentary support for BCLib (BCLib 1.21 has a lot of issues so I'm parking compat until they fix their issues)

## 4.0.0

- Complete overhaul of trim rendering system
  - Shader Renderer
    - The dynamic trims are by default rendered with a custom shader to improve performance and reduce memory footprint
    - This is incompatible with Iris / Optifine shaders so the Legacy renderer has also remained
  - Legacy Renderer
    - This renderer renders the trims the same way 3.x and below did by splitting the trim texture and applying a 
      colour to each layer, this is slower but compatible with Iris / Optifine shaders
    - Enables automatically when using a shader
- Trim Animations
  - Trims can now cycle through their colours and animate when rendering
  - Toggle "Animate Trim Rendering" in the config to enable
  - The speed and interpolation of the animations can be configured as well
- Trim Palettes
  - Trim palette generation now extracts the colour from the rendering of the item or block to allow for more 
    accurate colours
  - The colour choosing is slightly smarter and OkLab colour space is used when averaging or interpolating colours 
    for more vibrant palettes
  - Palette sorting can now be configured in the config as not all trim palettes look the same with the same sorting.
    You can choose one of brightness, colour, or saturation and reverse them if deseried
- Overriding
  - You can opt to have AllTheTrims override any provided trim materials such as vanilla's or another mod's to 
    use the palette generation instead
  - This is required if you want existing trim materials to animate
- Recipe Browser integration
  - REI/JEI/EMI support
  - Migrates trimming away from the smithing category to avoid clogging it up and creates it's own category 
    displaying the avaliable trimming recipes
- Debugging
  - Enabling debugging allows the generated model files and palettes to be exported to `.minecraft/att-debug` which 
    can be useful for diagnosing any missing texture issues
  - If shader rendering is enabled then the shader will display each layer of the trim with a different colour
- Compatible With
  - Any mod that adds trim templates or materials 
  - Show Me Your Skin
  - Elytra Trims
  - Wildfire's Gender Mod
  - Mythic Metals
