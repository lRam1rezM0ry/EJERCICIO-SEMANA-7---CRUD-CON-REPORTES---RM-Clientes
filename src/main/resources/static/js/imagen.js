/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
// Mostrar vista previa de la imagen seleccionada
document.getElementById("imagen").addEventListener("change", function(event) {
    const archivo = event.target.files[0];
    const preview = document.getElementById("previewImagen");

    if (archivo) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(archivo);
    } else {
        preview.src = "https://via.placeholder.com/150";
    }
});



