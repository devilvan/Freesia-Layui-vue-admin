import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {${dataBaseDto.className}Entity, ${dataBaseDto.className}Vo} from "@/types/${module}/${dataBaseDto.className}";

export function saveUpdate(${dataBaseDto.className?uncap_first}Vo: ${dataBaseDto.className}Vo): Promise<R<void>> {
 return Http.post("/api/${dataBaseDto.className?uncap_first}Controller/saveUpdate", ${dataBaseDto.className?uncap_first}Vo);
 }

export function saveUpdateBatch(${dataBaseDto.className?uncap_first}VoList: Array<${dataBaseDto.className}Vo>): Promise<R<void>> {
 return Http.post("/api/${dataBaseDto.className?uncap_first}Controller/saveUpdateBatch", ${dataBaseDto.className?uncap_first}VoList);
}

export function findPage${dataBaseDto.className}(${dataBaseDto.className?uncap_first}Vo: ${dataBaseDto.className}Vo, pageQuery: PageQuery): Promise<TableResult<${dataBaseDto.className}Entity>> {
 let params = buildPageUrlParam(${dataBaseDto.className?uncap_first}Vo, pageQuery);
 return Http.get("/api/${dataBaseDto.className?uncap_first}Controller/findPage${dataBaseDto.className}", params);
}

export function find${dataBaseDto.className}(${dataBaseDto.className?uncap_first}Vo: ${dataBaseDto.className}Vo): Promise<R<${dataBaseDto.className}Entity>> {
 let params = buildUrlParam(${dataBaseDto.className?uncap_first}Vo);
 return Http.get("/api/${dataBaseDto.className?uncap_first}Controller/find${dataBaseDto.className}", params);
}

export function findList${dataBaseDto.className}(${dataBaseDto.className?uncap_first}Vo: ${dataBaseDto.className}Vo): Promise<R<${dataBaseDto.className}Entity>> {
 let params = buildUrlParam(${dataBaseDto.className?uncap_first}Vo);
 return Http.get("/api/${dataBaseDto.className?uncap_first}Controller/findList${dataBaseDto.className}", params);
}

export function delete${dataBaseDto.className}(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/${dataBaseDto.className?uncap_first}Controller/delete${dataBaseDto.className}", idList);
}