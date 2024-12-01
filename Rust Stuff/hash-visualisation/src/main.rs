use hash_visualisation::{Evaluate, NodeBoolean, NodeColour, NodeNumeric};
fn main() {
    let new_node = Box::new(NodeNumeric::Add(
        Box::new(NodeNumeric::X),
        Box::new(NodeNumeric::Mult(
            Box::new(NodeNumeric::Y),
            Box::new(NodeNumeric::Number(-0.1)),
        )),
    ));
    let c = NodeColour {
        r: new_node,
        g: Box::new(NodeNumeric::If(
            Box::new(NodeBoolean::GreaterThan(
                Box::new(NodeNumeric::Number(0.2)),
                Box::new(NodeNumeric::Number(0.3)),
            )),
            Box::new(NodeNumeric::X),
            Box::new(NodeNumeric::Y),
        )),
        b: Box::new(NodeNumeric::Number(0.7)),
    };

    let (r, g, b) = c.eval(&0.5, &0.5).unwrap();
    println!("hello");
    println!("Node: {}", c);
    println!("Node at 0.5,0.5 ({}, {}, {})", r, g, b)
}
