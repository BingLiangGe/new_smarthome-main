package com.lj.iot.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 批注处理器
 *
 * @author zrj
 * @since 2021/12/29
 **/
public class CommentWriteHandler extends AbstractRowWriteHandler {

    /**
     * sheet名称KEY
     */
    public static final String SHEETNAME_NAME = "sheetName";

    /**
     * 文档后缀名
     */
    private String extension;

    /**
     * 列索引key
     */
    public static final String COLINDEX_NAME = "colIndex";

    /**
     * 行索引key
     */
    public static final String ROWINDEX_NAME = "rowIndex";

    /**
     * 批注内容key
     */
    public static final String COMMENTCONTENT_NAME = "commentContent";

    /**
     * sheet页名称列表
     */
    private List<String> sheetNameList;

    /**
     * 批注集合
     */
    List<Map<String, String>> commentList = new ArrayList<>(10);

    /**
     * CommentWriteHandler
     *
     * @param commentList
     * @param extension
     */
    public CommentWriteHandler(List<Map<String, String>> commentList, String extension) {
        this.commentList = commentList != null && commentList.size() > 0
                ? commentList.stream().filter(x ->
                x.keySet().contains(SHEETNAME_NAME) == true && x.get(SHEETNAME_NAME) != null && StrUtil.isNotBlank(x.get(SHEETNAME_NAME).toString())
                        && x.keySet().contains(COLINDEX_NAME) == true && x.get(COLINDEX_NAME) != null && StrUtil.isNotBlank(x.get(COLINDEX_NAME).toString())
                        && x.keySet().contains(ROWINDEX_NAME) == true && x.get(ROWINDEX_NAME) != null && StrUtil.isNotBlank(x.get(ROWINDEX_NAME).toString())
                        && x.keySet().contains(COMMENTCONTENT_NAME) == true && x.get(COMMENTCONTENT_NAME) != null && StrUtil.isNotBlank(x.get(COMMENTCONTENT_NAME).toString())
        ).collect(Collectors.toList()) : new ArrayList<>();
        sheetNameList = this.commentList.stream().map(x -> x.get(SHEETNAME_NAME).toString()).collect(Collectors.toList());
        this.extension = extension;
    }

    /**
     * 生成批注信息
     *
     * @param sheetName      sheet页名称
     * @param rowIndex       行号
     * @param columnIndex    列号
     * @param commentContent 批注内容
     * @return
     */
    public static Map<String, String> createCommentMap(String sheetName, int rowIndex, int columnIndex, String commentContent) {
        Map<String, String> map = new HashMap<>();
        //sheet页名称
        map.put(SHEETNAME_NAME, sheetName);
        //行号
        map.put(ROWINDEX_NAME, rowIndex + "");
        //列号
        map.put(COLINDEX_NAME, columnIndex + "");
        //批注内容
        map.put(COMMENTCONTENT_NAME, commentContent);
        return map;
    }

    /**
     * 功能描述
     * @param writeSheetHolder
     * @param writeTableHolder
     * @param row
     * @param relativeRowIndex
     * @param isHead
     * @return void
     */
    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = writeSheetHolder.getSheet();
        //不需要添加批注，或者当前sheet页不需要添加批注
        if (commentList == null || commentList.size() <= 0 || sheetNameList.contains(sheet.getSheetName()) == false) {
            return;
        }
        //获取当前行的批注信息
        List<Map<String, String>> rowCommentList = commentList.stream().filter(x ->
                StrUtil.equals(x.get(SHEETNAME_NAME).toString(), sheet.getSheetName())
                        && relativeRowIndex == NumberUtil.parseInt(x.get(ROWINDEX_NAME))).collect(Collectors.toList());
        //当前行没有批注信息
        if (rowCommentList == null || rowCommentList.size() <= 0) {
            return;
        }
        List<String> colIndexList = rowCommentList.stream().map(x -> x.get(COLINDEX_NAME)).distinct().collect(Collectors.toList());
        for (String colIndex : colIndexList) {
            //同一单元格的批注信息
            List<Map<String, String>> cellCommentList = rowCommentList.stream().filter(x ->
                    StrUtil.equals(colIndex, x.get(COLINDEX_NAME))).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(cellCommentList)) {
                continue;
            }
            //批注内容拼成一条
            String commentContent = cellCommentList.stream().map(x -> x.get(COMMENTCONTENT_NAME)).collect(Collectors.joining());
            Cell cell = row.getCell(Integer.parseInt(colIndex));
            addComment(cell, commentContent, extension);
        }
        //删除批注信息
        commentList.remove(rowCommentList);
        //重新获取要添加的sheet页姓名
        sheetNameList = commentList.stream().map(x -> x.get(SHEETNAME_NAME).toString()).collect(Collectors.toList());
    }

    /**
     * 给Cell添加批注
     *
     * @param cell      单元格
     * @param value     批注内容
     * @param extension 扩展名
     */
    public static void addComment(Cell cell, String value, String extension) {
        Sheet sheet = cell.getSheet();
        cell.removeCellComment();
        if ("xls".equals(extension)) {
            ClientAnchor anchor = new HSSFClientAnchor();
            // 关键修改
            anchor.setDx1(0);
            anchor.setDx2(0);
            anchor.setDy1(0);
            anchor.setDy2(0);
            anchor.setCol1(cell.getColumnIndex());
            anchor.setRow1(cell.getRowIndex());
            anchor.setCol2(cell.getColumnIndex() + 5);
            anchor.setRow2(cell.getRowIndex() + 6);
            // 结束
            Drawing drawing = sheet.createDrawingPatriarch();
            Comment comment = drawing.createCellComment(anchor);
            // 输入批注信息
            comment.setString(new HSSFRichTextString(value));
            // 将批注添加到单元格对象中
            cell.setCellComment(comment);
        } else if ("xlsx".equals(extension)) {
            ClientAnchor anchor = new XSSFClientAnchor();
            // 关键修改
            anchor.setDx1(0);
            anchor.setDx2(0);
            anchor.setDy1(0);
            anchor.setDy2(0);
            anchor.setCol1(cell.getColumnIndex());
            anchor.setRow1(cell.getRowIndex());
            anchor.setCol2(cell.getColumnIndex() + 5);
            anchor.setRow2(cell.getRowIndex() + 6);
            // 结束
            Drawing drawing = sheet.createDrawingPatriarch();
            Comment comment = drawing.createCellComment(anchor);
            // 输入批注信息
            comment.setString(new XSSFRichTextString(value));
            // 将批注添加到单元格对象中
            cell.setCellComment(comment);
        }
    }
}
