import Http from "../Http";
import {R} from "../../types/Result";
import {RpFindAllocAmountDto} from "../../types/account/AccountCostUserAlloc";

export function findAllocAmount(): Promise<R<RpFindAllocAmountDto>> {
    return Http.post("/api/accountCostUserAllocController/findAllocAmount");
}
