import { Card, Paper, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TablePagination, TableRow } from "@mui/material";
import { TransactionDto } from "../../api/dtos"
import React from "react";

type Props = {
    transactions: TransactionDto[];
};

const TransactionsTable = ({ transactions }: Props) => {
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
        <TableContainer sx={{ maxWidth: 1256, maxHeight: 512, overflow: 'auto', scrollbarWidth: 'thin' }}>
            <Table stickyHeader>
                <TableHead>
                    <TableRow>
                        <TableCell>Reference from institution</TableCell>
                        <TableCell align="justify">Amount&nbsp;($)</TableCell>
                        <TableCell align="justify">Booking date</TableCell>
                        <TableCell align="justify">Information</TableCell>
                        <TableCell align="justify">Categories</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {transactions
                        .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                        .map((transaction, index) => (
                            <TableRow
                                key={index}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                                <TableCell component="th" scope="row">
                                    {transaction.refFromInstitution}
                                </TableCell>
                                <TableCell align="justify">{transaction.amount}</TableCell>
                                <TableCell align="justify">{transaction.bookingDate.toString()}</TableCell>
                                <TableCell align="justify">{transaction.remittanceInfo || 'None'}</TableCell>
                                <TableCell align="justify">
                                    <div style={{ display: 'flex', overflow: 'auto' }}>
                                        {transaction.categories.length !== 0 && transaction.categories.map((category, index) => (
                                            <Card key={index} sx={{ height: '10px', width: '10px', backgroundColor: `${category.colorCode}`, margin: '2px' }} />
                                        ))}
                                        {transaction.categories.length === 0 && 'None'}
                                    </div>
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