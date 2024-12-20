package com.example.clientes.controller;

import com.example.clientes.model.Cliente;
import com.example.clientes.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

// Importaciones para PDF
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Cell;


// Importaciones para Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", service.listarClientes());
        return "clientes";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "formularioCliente";
    }

@PostMapping
public String guardarCliente(@ModelAttribute Cliente cliente) {
    try {
        // Verificamos si hay un archivo de imagen
        if (cliente.getArchivoImagen() != null && !cliente.getArchivoImagen().isEmpty()) {
            // Convertimos el archivo MultipartFile a byte[]
            cliente.setImagen(cliente.getArchivoImagen().getBytes());
        } else if (cliente.getIdCliente() != null) {
            // Si es una actualización y no hay nueva imagen, mantenemos la imagen existente
            Cliente clienteExistente = service.buscarClientePorId(cliente.getIdCliente())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + cliente.getIdCliente()));
            cliente.setImagen(clienteExistente.getImagen());
        }

        // Guardamos el cliente
        service.guardarCliente(cliente);
    } catch (Exception e) {
        e.printStackTrace();
        return "error"; // Redirigir a una página de error si algo falla
    }
    return "redirect:/clientes"; // Redirigir al listado de clientes después de guardar
}

    
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable String id, Model model) {
        model.addAttribute("cliente", service.buscarClientePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id)));
        return "formularioCliente";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable String id) {
        service.eliminarCliente(id);
        return "redirect:/clientes";
    }



@GetMapping("/reporte/pdf")
public void generarPdf(HttpServletResponse response) throws IOException {
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "inline; filename=clientes_reporte.pdf");

    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(response.getOutputStream()));
    Document document = new Document(pdfDoc);

    document.add(new Paragraph("Reporte de Clientes").setBold().setFontSize(18));

    Table table = new Table(8);
    table.addCell("ID");
    table.addCell("Nombre");
    table.addCell("Tipo Documento");
    table.addCell("N° Documento");
    table.addCell("Teléfono");
    table.addCell("Dirección");
    table.addCell("Correo");
    table.addCell("Imagen");

    // Convertir Iterable a List
    List<Cliente> clientes = StreamSupport.stream(service.listarClientes().spliterator(), false)
                                           .collect(Collectors.toList());

    for (Cliente cliente : clientes) {
        table.addCell(cliente.getIdCliente());
        table.addCell(cliente.getNombre());
        table.addCell(cliente.getTipoDocumento());
        table.addCell(cliente.getNumeroDocumento());
        table.addCell(cliente.getTelefono());
        table.addCell(cliente.getDireccion());
        table.addCell(cliente.getCorreo());

        if (cliente.getImagen() != null) {
            Image image = new Image(ImageDataFactory.create(cliente.getImagen()));
            image.setAutoScale(true);
            Cell imageCell = new Cell().add(image.setWidth(50).setHeight(50));
            table.addCell(imageCell);
        } else {
            table.addCell("Sin Imagen");
        }
    }

    document.add(table);
    document.close();
}


@GetMapping("/reporte/excel")
public void generarExcel(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=clientes_reporte.xlsx");

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Clientes");

    // Ajustar ancho de las columnas
    sheet.setColumnWidth(0, 6000); // ID
    sheet.setColumnWidth(1, 8000); // Nombre
    sheet.setColumnWidth(2, 8000); // Tipo Documento
    sheet.setColumnWidth(3, 8000); // N° Documento
    sheet.setColumnWidth(4, 6000); // Teléfono
    sheet.setColumnWidth(5, 10000); // Dirección
    sheet.setColumnWidth(6, 10000); // Correo
    sheet.setColumnWidth(7, 5000); // Imagen (reducido el ancho)

    // Crear estilo para encabezados
    CellStyle headerStyle = workbook.createCellStyle();
    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerStyle.setFont(headerFont);
    headerStyle.setBorderTop(BorderStyle.THIN);
    headerStyle.setBorderBottom(BorderStyle.THIN);
    headerStyle.setBorderLeft(BorderStyle.THIN);
    headerStyle.setBorderRight(BorderStyle.THIN);
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    // Crear estilo para contenido (aumentando el tamaño de la fuente)
    CellStyle contentStyle = workbook.createCellStyle();
    Font contentFont = workbook.createFont();
    contentFont.setFontHeightInPoints((short) 15); // Aumentar tamaño de fuente a 15 puntos
    contentStyle.setFont(contentFont);
    contentStyle.setBorderTop(BorderStyle.THIN);
    contentStyle.setBorderBottom(BorderStyle.THIN);
    contentStyle.setBorderLeft(BorderStyle.THIN);
    contentStyle.setBorderRight(BorderStyle.THIN);
    contentStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar el texto
    contentStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar el texto verticalmente

    // Crear fila de encabezados
    Row headerRow = sheet.createRow(0);
    String[] columnHeaders = {"ID", "Nombre", "Tipo Documento", "N° Documento", "Teléfono", "Dirección", "Correo", "Imagen"};
    for (int i = 0; i < columnHeaders.length; i++) {
        org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
        cell.setCellValue(columnHeaders[i]);
        cell.setCellStyle(headerStyle);
    }

    // Ajustar altura de la fila de encabezado
    headerRow.setHeightInPoints(25); // Ajuste de altura para los encabezados

    // Llenar datos de clientes
    List<Cliente> clientes = StreamSupport
            .stream(service.listarClientes().spliterator(), false)
            .collect(Collectors.toList());

    int rowIndex = 1;
    for (Cliente cliente : clientes) {
        Row row = sheet.createRow(rowIndex++);

        // Ajustar altura de las filas de contenido
        row.setHeightInPoints(100); // Ajuste de altura para las filas de contenido

        org.apache.poi.ss.usermodel.Cell cellId = row.createCell(0);
        cellId.setCellValue(cliente.getIdCliente());
        cellId.setCellStyle(contentStyle);

        org.apache.poi.ss.usermodel.Cell cellNombre = row.createCell(1);
        cellNombre.setCellValue(cliente.getNombre());
        cellNombre.setCellStyle(contentStyle);

        org.apache.poi.ss.usermodel.Cell cellTipoDocumento = row.createCell(2);
        cellTipoDocumento.setCellValue(cliente.getTipoDocumento());
        cellTipoDocumento.setCellStyle(contentStyle);

        org.apache.poi.ss.usermodel.Cell cellNumeroDocumento = row.createCell(3);
        cellNumeroDocumento.setCellValue(cliente.getNumeroDocumento());
        cellNumeroDocumento.setCellStyle(contentStyle);

        org.apache.poi.ss.usermodel.Cell cellTelefono = row.createCell(4);
        cellTelefono.setCellValue(cliente.getTelefono());
        cellTelefono.setCellStyle(contentStyle);

        org.apache.poi.ss.usermodel.Cell cellDireccion = row.createCell(5);
        cellDireccion.setCellValue(cliente.getDireccion());
        cellDireccion.setCellStyle(contentStyle);

        org.apache.poi.ss.usermodel.Cell cellCorreo = row.createCell(6);
        cellCorreo.setCellValue(cliente.getCorreo());
        cellCorreo.setCellStyle(contentStyle);

        // Agregar imagen si está presente
        if (cliente.getImagen() != null) {
            int pictureIdx = workbook.addPicture(cliente.getImagen(), Workbook.PICTURE_TYPE_JPEG);
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(7);
            anchor.setRow1(row.getRowNum());
            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize(0.6);
        }
    }

    // Escribir el archivo
    workbook.write(response.getOutputStream());
    workbook.close();
}


}