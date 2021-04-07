package cc.lingenliu.example.document.application.assembler

import cc.lingenliu.example.document.api.DocumentDTO
import cc.lingenliu.example.document.domain.Document
import cc.lingenliu.example.document.domain.DocumentType

fun toDocument(documentDTO: DocumentDTO):Document{
    val document = Document()
    document.mediaId = documentDTO.mediaId
    document.name = documentDTO.name
    document.documentType = DocumentType.valueOf(documentDTO.documentType)
    document.md5 = documentDTO.md5
    document.suffix = documentDTO.suffix
    return document
}

fun toDocumentDTO(document: Document):DocumentDTO{
    return DocumentDTO(
        id = document.id,
        mediaId = document.mediaId,
        name = document.name,
        documentType = document.documentType.toString(),
        md5 = document.md5,
        suffix = document.suffix
    )
}