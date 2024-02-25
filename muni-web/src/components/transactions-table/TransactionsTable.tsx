import { Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TablePagination, TableRow, Tooltip } from "@mui/material";
import React from "react";
import { CategoryDto, TransactionDto } from "../../api/dtos";
import TransactionCategories from "../transaction-categories/TransactionCategories";

type Props = {
    transactions: TransactionDto[];
    categories: CategoryDto[];
};

const TransactionsTable = ({ transactions, categories }: Props) => {
    const [page, setPage] = React.useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = React.useState<number>(10);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    }

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    return (
        <TableContainer sx={{ maxWidth: 1256, maxHeight: 512, overflowY: 'auto', scrollbarWidth: 'thin' }}>
            <Table stickyHeader>
                <TableHead>
                    <TableRow>
                        <TableCell align="justify">Institution</TableCell>
                        <TableCell align="justify">Reference from institution</TableCell>
                        <TableCell align="justify">Account IBAN</TableCell>
                        <TableCell align="justify">Amount&nbsp;($)</TableCell>
                        <TableCell align="justify">Booking date</TableCell>
                        <TableCell align="justify">Information</TableCell>
                        <TableCell align="justify">Categories</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {transactions.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                        .map((transaction, index) => (
                            <TableRow key={index} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                <TableCell scope="row" component="th">
                                    <Tooltip key={transaction.externalId} title={transaction.institutionName}>
                                        <img src={transaction.institutionLogo} height='40px' width='40px' />
                                    </Tooltip>
                                </TableCell>
                                <TableCell align="justify" component="th">{transaction.refFromInstitution}</TableCell>
                                <TableCell align="justify">{transaction.accountIban}</TableCell>
                                <TableCell align="justify">{transaction.amount}</TableCell>
                                <TableCell align="justify">{transaction.bookingDate.toString()}</TableCell>
                                <TableCell align="justify">{transaction.remittanceInfo || 'None'}</TableCell>
                                <TableCell align="justify">
                                    <TransactionCategories transaction={transaction} customerCategories={categories} />
                                </TableCell>
                            </TableRow>
                        ))}
                </TableBody>
                <TableFooter>
                    <TableRow>
                        <TablePagination
                            rowsPerPageOptions={[10, 25, 100]}
                            count={transactions.length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </TableRow>
                </TableFooter>
            </Table>
        </TableContainer>
    );
}

export default TransactionsTable;